# Brief R11 — Données de démonstration NOVA (réalistes, Madagascar)

- Branch: infra/feat-nova-demo-data (created by the orchestrator from the global test branch test/nova-integration once all integrations are merged there)
- Worktree: /home/saandria/Documents/devfest/erp-of-tomorrow.worktrees/infra-feat-nova-demo-data
- Scratch dir: <SCRATCH>/work/R11
- Location in repo: `scripts/demo/` (Python 3 stdlib only: urllib, json; one module per domain, each file <= 100 lines, no comments, no docstrings), entry `python3 scripts/demo/seed.py --gateway http://localhost:8080 --email <admin> --password <pwd>`, idempotent (re-run creates nothing new), plus `scripts/demo/backdate.sql` applied with `docker compose exec -T erp-db psql -U postgres -d erp -f -` to spread created/validated timestamps over the last 60 days (API calls always stamp "now"); README section "Données de démonstration".

## Content (coherent with the Figma screens, all through the gateway API with the registry contracts)
- Accounts: admin full name "Hery Rabe" (ADMIN_NAME), Rasoa Andria (VENTE, rasoa@boutique-tana.mg), Nirina Rabe (STOCK), Mamy Razafy (RH), Fara Andria (CRM); password from `--demo-password` (default documented as demo-only).
- Départements: Logistique, Stock, Ventes, Administration. ~37 employees (Toky Rakoto Chauffeur CDI 2021-03, Haja Randria Chauffeur CDI 2022-06, Nirina Rabe Magasinière CDD 2024-01, Fara Andria Vendeuse CDI 2019-09, Mamy Razafy Comptable CDI 2018-02, Rado, ...), baseSalary realistic in Ariary (300 000 – 1 200 000), 3 validated leaves covering today, 3 DRAFT leave requests 23–27 décembre, 6 salary advances this month (2 DRAFT).
- Catégories et ~64 produits (Bougies (paquet de 6) Maison 4 u. seuil 10, Huile 5 L Épicerie sèche 18 u. seuil 20, Lait concentré 397 g Produits laitiers 96 u. seuil 24, Riz blanc 25 kg 40 sacs seuil 10, Sucre 1 kg 220 u. seuil 50, ...) with purchasePrice, about 5 under threshold, 2 at rupture risk; lots LOT-2610-C (2027-01-03), LOT-2609-F (expiring in ~23 days), LOT-2611-A, LOT-2611-B; 12 suppliers (Tiko Distribution Antananarivo, ...) with city/NIF/STAT; 3 purchase orders VALIDATED not received.
- ~214 customers (Épicerie Rova, Boutique Soa, Hôtel Tsara, Supérette Mahery, Hanta Andriamanana, Épicerie Rasoa, Mme Ravao, Boutique Fanilo, Andry Rakoto, Snack Mamy...) across Antananarivo, Antsirabe, Toamasina, Mahajanga, Fianarantsoa; 58 ENTREPRISE with NIF/STAT, some with credit limits (Supérette Mahery 1 500 000 Ar exceeded).
- Sales over the month (a few hundred, scaled down if slow) with payment methods (Espèces, MVola, Orange Money, Crédit 15/30 jours), payments, 5 cancellations, 9 TO_COLLECT, some OVERDUE, 9 marked toDeliver this week; CRM interactions so that 3 contacts are "À relancer".
- Adaptations: NONE activated by default (the demo shows the Eclipse flow live); option `--with-adaptations` activates the 4 catalog adaptations.
- Custom module Livraisons: NOT created by default (created live in the demo); option `--with-livraisons` creates it via the template definition and imports the 9 sales, then advances 7 to Livrée and 2 to En route (livreurs Rado and Fara).

## Acceptance
Fresh stack + migrations + seed => every module screen shows realistic data close to the Figma numbers; re-running seed is a no-op; backdate script idempotent.
