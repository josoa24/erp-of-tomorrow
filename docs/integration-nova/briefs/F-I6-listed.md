# Follow-up F-I6 — colonne `listed` (registry D2)

- Worktree: /home/saandria/Documents/devfest/erp-of-tomorrow.worktrees/modules-feat-module-engine
- Branch: modules/feat-module-engine
- Scratch dir: <SCRATCH>/work/F-I6
- Read registry D2 in docs/integration-nova/registry.md.

Add `listed: boolean` (default true when omitted) to the module column spec: request DTO, response DTO, entity column `listed boolean NOT NULL DEFAULT true`, mapper, validation (at least one listed column), tests (controller + service + mapper). Idempotent migration `docker/postgres/migrations/10x_module_column_listed.sql` (next free number in 100–149) adding the column with default true on existing rows. Update the module-service section of docs/API_ENDPOINTS.md. mvn test (Docker) green. Small commits with the usual rules.
