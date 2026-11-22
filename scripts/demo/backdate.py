"""Étale l'historique sur 4 semaines, pour que la prévision et le chiffre d'affaires aient un passé.

Les dates sont posées par les services au moment de l'écriture : un jeu de démo créé en une minute
aurait tout « aujourd'hui ». On répartit donc, dans la base, les mouvements de stock sur les 27
derniers jours (le plus ancien d'abord, dans l'ordre de création) et les ventes de la même façon.
Réservé aux données de démonstration : ne jamais lancer sur une base réelle.
"""
from __future__ import annotations

import subprocess

DAYS = 27
SPREAD_SQL = """
WITH ordered AS (
  SELECT id, row_number() OVER (ORDER BY id) AS rn, count(*) OVER () AS total FROM {table}
)
UPDATE {table} t
SET {columns}
FROM ordered o WHERE t.id = o.id;
"""
SHIFT = "now() - ((o.total - o.rn) * interval '{days} days' / greatest(o.total, 1)) - interval '1 hour'"


def _spread(table: str, columns: list[str]) -> str:
    shift = SHIFT.format(days=DAYS)
    return SPREAD_SQL.format(table=table, columns=", ".join(f"{c} = {shift}" for c in columns))


def backdate_history(service: str = "erp-db") -> None:
    sql = "BEGIN;" + _spread("stock.stock_movements", ["created_at"]) \
        + _spread("vente.sales", ["created_at", "validated_at"]) + "COMMIT;"
    command = ["docker", "compose", "exec", "-T", service, "psql", "-v", "ON_ERROR_STOP=1",
               "-U", "postgres", "-d", "erp", "-q", "-c", sql]
    result = subprocess.run(command, capture_output=True, text=True)
    if result.returncode != 0:
        raise RuntimeError("Antidatage impossible (base lancée avec docker compose ?) : " + result.stderr.strip())
    print(f"  mouvements de stock et ventes répartis sur les {DAYS} derniers jours")
