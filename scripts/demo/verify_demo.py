"""Vérifie l'ERP de bout en bout via la gateway, sans l'assistant IA, après seed_demo.py.

Usage : python scripts/demo/verify_demo.py [--gateway http://localhost:8080]
Code de sortie 0 si tous les contrôles passent, 1 sinon.
"""
from __future__ import annotations

import argparse
import os
import sys

from api import Api
from checks import Checks
from verify_business import verify_hr_and_crm, verify_invoice_shield, verify_sales, verify_stock
from verify_platform import verify_platform
from verify_users import verify_team_accounts


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("--gateway", default=os.getenv("GATEWAY_URL", "http://localhost:8080"))
    parser.add_argument("--email", default=os.getenv("ADMIN_EMAIL", "admin@erp.local"))
    parser.add_argument("--password", default=os.getenv("ADMIN_PASSWORD", "admin1234"))
    args = parser.parse_args()

    checks = Checks()
    admin = Api(args.gateway).login(args.email, args.password)
    users = verify_platform(checks, args.gateway, admin)
    verify_stock(checks, users["STOCK"])
    verify_invoice_shield(checks, users["STOCK"], admin)
    verify_sales(checks, users["VENTE"])
    verify_hr_and_crm(checks, users["RH"], users["CRM"])
    verify_team_accounts(checks, args.gateway, admin)
    return checks.summary()


if __name__ == "__main__":
    sys.exit(main())
