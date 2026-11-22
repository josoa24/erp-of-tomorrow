# Follow-up F-I7 — alignement ai-service sur le moteur de modules (registry D1, D2, D3)

- Worktree: /home/saandria/Documents/devfest/erp-of-tomorrow.worktrees/eclipse-feat-eclipse-flows
- Branch: eclipse/feat-eclipse-flows
- Scratch dir: <SCRATCH>/work/F-I7
- Read registry D1–D3 in docs/integration-nova/registry.md and C1 in registry-part-modules-eclipse.md.

1. D2: the ModuleDefinitionRequest model gains `listed` (default true); templates set `listed: false` on Vente d'origine, Montant à encaisser and Signature du client for Livraisons (and similar non-list columns for Réparations / Rendez-vous); remove `hiddenColumns` from the modules/propose response (keep `uncheckedColumns` and `note`).
2. D3: make ai-service definition validation exactly as strict as module-service's validator: read it on branch modules/feat-module-engine (`git show modules/feat-module-engine:services/module-service/src/main/java/...` — find the validator classes with `git ls-tree -r --name-only modules/feat-module-engine services/module-service | grep -i valid`) and mirror every rule (icon allow-list, key format, column counts, statuses, indicators kinds/columns/todayColumn/detailColumn, CHOICE/REFERENCE options, listed). Add a test per rule.
3. D1: confirm CREATE_MODULE is only returned for explicit creation requests and never when a catalog adaptation keyword matches (tests with "Ajoute dans ce module le n° de lot…").
pytest green (no key). Small commits with the usual rules. Update docs/API_ENDPOINTS.md section 10 accordingly.
