"""Remplit l'ERP avec un jeu de démonstration complet, via la gateway.

Usage (base neuve, ERP lancé avec docker compose) :
    docker compose down -v && docker compose up -d
    python scripts/demo/seed_demo.py [--gateway http://localhost:8080] [--no-backdate]

Crée : comptes de chaque rôle (mot de passe demo12345), produits, fournisseurs avec compte de paiement,
bon de commande reçu, clients, 56 ventes étalées sur 4 semaines, RH, CRM, une facture saine (CLEARED)
et une facture piégée (BLOCKED par Invoice Shield). Relancer le script ne crée pas de doublon.
"""
from __future__ import annotations

import argparse
import os
import sys

from api import Api
from backdate import backdate_history
from data import PASSWORD
from seed_core import seed_accounts, seed_customers, seed_products, seed_received_order, seed_sales, seed_suppliers
from seed_more import seed_crm, seed_hr, seed_invoices


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("--gateway", default=os.getenv("GATEWAY_URL", "http://localhost:8080"))
    parser.add_argument("--email", default=os.getenv("ADMIN_EMAIL", "admin@erp.local"))
    parser.add_argument("--password", default=os.getenv("ADMIN_PASSWORD", "admin1234"))
    parser.add_argument("--no-backdate", action="store_true", help="ne pas étaler l'historique dans la base")
    args = parser.parse_args()

    admin = Api(args.gateway).login(args.email, args.password)
    print("Comptes et stock :")
    user_ids = seed_accounts(admin)
    products = seed_products(admin)
    suppliers = seed_suppliers(admin)
    order = seed_received_order(admin, products, suppliers)
    print("Ventes :")
    customers = seed_customers(admin)
    seed_sales(admin, products, customers)
    if not args.no_backdate:
        backdate_history()
    print("RH et CRM :")
    seed_hr(admin, user_ids)
    seed_crm(admin, customers, suppliers)
    print("Factures fournisseurs (saisies par le compte STOCK) :")
    seed_invoices(Api(args.gateway).login("stock@erp.local", PASSWORD), suppliers, products, order)
    print("Terminé. Comptes : admin@erp.local, stock@, vente@, rh@, crm@erp.local (mot de passe " + PASSWORD + ").")
    return 0


if __name__ == "__main__":
    sys.exit(main())
