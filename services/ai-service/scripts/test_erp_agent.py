"""Test de bout en bout de l'assistant IA, comme le ferait un front (aucune dépendance).

Chaîne testée : login -> gateway -> ai-service -> Gemini -> outils -> services ERP (avec le token).
Les effets des écritures sont vérifiés directement dans l'ERP (stock avant / après).

Prérequis : bases + services Java + gateway + ai-service démarrés, puis `python scripts/seed_demo.py`.
Usage     : python scripts/test_erp_agent.py [--gateway http://localhost:8080]
"""
from __future__ import annotations

import argparse
import os
import time

from erp_http import Api

AI = "/api/ai/agent"
results: list[tuple[str, bool, str]] = []


def check(name: str, ok: bool, detail: str = "") -> bool:
    results.append((name, ok, detail))
    print(f"  [{'OK' if ok else 'KO'}] {name}" + (f" — {detail}" if detail else ""))
    return ok


def ask(api: Api, message: str, conv: str | None = None) -> dict:
    """Envoie un message en streaming et résume le flux (texte, outils, saisies en attente, temps)."""
    t0 = time.perf_counter()
    events = api.stream(f"{AI}/chat/stream", {"message": message, "conversation_id": conv})
    done = next((d for e, d in events if e == "done"), {})
    error = next((d for e, d in events if e == "error"), None)
    tools = [d for e, d in events if e == "tool_result"]
    first = next((i for i, (e, _) in enumerate(events) if e == "token"), None)
    print(f"\n  > {message}\n  < {(done.get('response') or str(error))[:300]}")
    print(f"    outils : {', '.join(t['name'] + ('[' + t['status'] + ']' if t.get('status') else '') for t in tools) or 'aucun'}"
          f" | {(time.perf_counter() - t0) * 1000:.0f} ms | 1er mot {done.get('metrics', {}).get('first_token_ms', '?')} ms")
    return {"conv": done.get("conversation_id") or (events[0][1].get("conversation_id") if events else conv),
            "tools": tools, "names": [t["name"] for t in tools], "done": done, "error": error,
            "pending": done.get("pending_actions", [])}


def stock_of(admin: Api, sku: str) -> int | None:
    _, items = admin.request("GET", f"/api/stock/products?search={sku}")
    return next((p["quantity"] for p in items or [] if p["sku"] == sku), None)


def main() -> int:
    ap = argparse.ArgumentParser()
    ap.add_argument("--gateway", default=os.getenv("GATEWAY_URL", "http://localhost:8080"))
    ap.add_argument("--email", default=os.getenv("ADMIN_EMAIL", "admin@erp.local"))
    ap.add_argument("--password", default=os.getenv("ADMIN_PASSWORD", "admin1234"))
    a = ap.parse_args()
    anon = Api(a.gateway)
    admin = anon.login(a.email, a.password)

    print("\n1. Accès et sécurité")
    check("sans token -> 401", anon.request("POST", f"{AI}/chat", {"message": "x"})[0] == 401)
    status, tools = admin.request("GET", "/api/ai/tools")
    check("ai-service joignable via la gateway (/api/ai)", status == 200, f"{(tools or {}).get('count')} outils ADMIN")
    check("page de test publique /api/ai/ui", anon.request("GET", "/api/ai/ui")[0] == 200)

    print("\n2. Lecture")
    r = ask(admin, "Quels produits risquent la rupture de stock ?")
    check("risque de rupture -> get_stock_status", "get_stock_status" in r["names"] or "search_products" in r["names"])
    r = ask(admin, "Quel est le chiffre d'affaires de ce mois ?")
    check("CA du mois -> get_sales_summary", "get_sales_summary" in r["names"])
    r2 = ask(admin, "Et depuis le début ?", r["conv"])
    check("contexte conservé (« et depuis le début ? »)", "get_sales_summary" in r2["names"])
    r = ask(admin, "Quels sont les 3 produits les plus vendus ?")
    check("top produits -> get_top_products", "get_top_products" in r["names"])
    r = ask(admin, "Bonjour !")
    check("salutation sans outil", r["names"] == [])

    print("\n3. Écriture confirmée par « oui » (entrée de stock)")
    before = stock_of(admin, "RIZ-25")
    r = ask(admin, "J'ai reçu 10 sacs de riz blanc 25 kg, enregistre l'entrée de stock.")
    pending = [p for p in r["pending"] if p["tool"] == "record_stock_movement"]
    check("entrée proposée, pas exécutée", bool(pending) and stock_of(admin, "RIZ-25") == before,
          f"stock {before} inchangé")
    r = ask(admin, "Oui, je confirme.", r["conv"])
    after = stock_of(admin, "RIZ-25")
    check("après « oui » : stock +10", after == (before or 0) + 10, f"{before} -> {after}")

    print("\n4. Écriture refusée (« non »)")
    before = stock_of(admin, "HUI-5")
    r = ask(admin, "Enregistre une sortie de 2 bidons d'huile 5 L pour casse.")
    check("sortie proposée", any(p["tool"] == "record_stock_movement" for p in r["pending"]))
    ask(admin, "Non, annule.", r["conv"])
    check("après « non » : stock inchangé", stock_of(admin, "HUI-5") == before, f"stock {before}")

    print("\n5. Écriture confirmée par le bouton (vente)")
    before = stock_of(admin, "FAR-50")
    r = ask(admin, "Vends 1 sac de farine 50 kg à l'Épicerie du Marché.")
    sale = next((p for p in r["pending"] if p["tool"] == "create_sale"), None)
    if check("vente proposée", sale is not None and stock_of(admin, "FAR-50") == before):
        status, body = admin.request("POST", f"{AI}/conversations/{r['conv']}/actions/{sale['action_id']}/confirm")
        check("bouton Confirmer -> vente validée", status == 200 and (body or {}).get("status") == "VALIDATED",
              f"HTTP {status} vente #{(body or {}).get('id')}")
        check("stock farine -1", stock_of(admin, "FAR-50") == (before or 0) - 1)
        status, _ = admin.request("POST", f"{AI}/conversations/{r['conv']}/actions/{sale['action_id']}/confirm")
        check("double confirmation refusée", status == 404)

    print("\n6. Droits par rôle (compte STOCK)")
    try:
        stock_user = anon.login("stock@erp.local", "demo12345")
    except SystemExit:
        check("compte stock@erp.local", False, "lancer d'abord scripts/seed_demo.py")
    else:
        _, t = stock_user.request("GET", "/api/ai/tools")
        modules = {x["module"] for x in (t or {}).get("tools", [])}
        check("outils du rôle STOCK uniquement", modules == {"stock", "system"}, str(sorted(modules)))
        r = ask(stock_user, "Quel est le chiffre d'affaires de ce mois ?")
        check("pas d'accès aux ventes", not any(n.startswith("get_sales") for n in r["names"]))
        status, _ = stock_user.request("GET", f"{AI}/conversations/{r2['conv']}")
        check("conversation d'un autre utilisateur invisible", status == 404)

    ok = sum(1 for _, passed, _ in results if passed)
    print(f"\n{'=' * 60}\nBilan : {ok}/{len(results)} vérifications réussies")
    for name, passed, detail in results:
        if not passed:
            print(f"  KO : {name} {detail}")
    return 0 if ok == len(results) else 1


if __name__ == "__main__":
    raise SystemExit(main())
