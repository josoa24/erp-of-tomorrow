# Brief I5 — Écran Équipe (07 — Ressources humaines) de bout en bout

- Worktree: /home/saandria/Documents/devfest/erp-of-tomorrow.worktrees/rh-feat-nova-equipe
- Branch: rh/feat-nova-equipe
- Scratch dir: <SCRATCH>/work/I5
- Migration prefix: 500–549
- Figma: 07-equipe-generique_107-395.png, 07-equipe-apres-ia_107-405.png (after « Ajoute les avances sur salaire, plafonnées à 30 % du salaire »). Nodes 107:395 / 107:405.
- Registry: you own R7 = H1–H4 in docs/integration-nova/registry.md. You may touch only services/rh-service, your migrations, the RH section of docs/API_ENDPOINTS.md, novaui/src/features/team/.
- Analysis: docs/integration-nova/analyses/rh-crm-auth.md

## PHASE A (backend)
H1–H3 with Reader/Creator/Validator/Rejecter services + interfaces, DTOs, typed errors (new ForbiddenException -> 403 FORBIDDEN for non-ADMIN advance decisions), tests (cap boundaries: exactly 30 % allowed, 30 % + 1 Ar refused; validation re-check; IMMUTABLE on decided; month boundaries in Indian/Antananarivo; onLeaveToday at 22:00 UTC the previous day). Migrations 500_rh_contract_type.sql, 501_rh_leave_decided_by.sql, 502_rh_salary_advances.sql (FK employee, CHECK amount > 0, CHECK status in list, index (employee_id, month)). Keep summary backward compatible. mvn test via Docker, then real-DB check with a throwaway postgres container (rh-service only).

## PHASE B (frontend)
Replace stub `features/team/screens/TeamScreen` with ModulePage: header "Équipe", subtitle "<n> personnes · <m> en congé aujourd'hui", SearchField "Rechercher un employé…", primary "Ajouter un employé" (Dialog: prénom, nom, e-mail, poste, service (departments select), contrat, date d'arrivée <= today, salaire de base; POST; toast). KPIs: Effectif (total, "<n> services", Users), En congé ("aujourd'hui", CalendarDays), Demandes (pendingLeaves, "congés à valider", ClipboardList — clickable, opens the "Demandes de congé" Dialog listing DRAFT leaves with employee name and dates, actions Valider / Refuser per row with confirmation; also opened by `?vue=demandes`), with adaptation Avances du mois (monthTotal, "<n> demandes · <m> à valider", Percent, highlighted in preview). Filters (URL): Tous / Présents / En congé (+ Avance demandée). Sort hint "Triés par service". Columns: Employé (avatar initials + name + position sub-line), Service, Contrat ("—" if null), Arrivée ("mars 2021"), Statut (Présent success Check / En congé info Info) and with adaptation: Avance du mois ("150 000 Ar", "80 000 Ar · à valider", "—"), Plafond 30 % (isNew in preview). Row menu: "Demander un congé" (Dialog dates + reason), with adaptation "Demander une avance" (Dialog amount <= remaining cap shown as helper) and, for ADMIN when the employee has a DRAFT advance, "Valider l'avance" / "Refuser l'avance". Eclipse placeholder « Demandez ou adaptez « Équipe »… ex. « qui est en congé la semaine prochaine ? » »; adaptation via `useModuleAdaptation('equipe')`. Role RH | ADMIN. States, responsive, tests, visual check, real-stack check when available.

## Acceptance
Generic matches 107:395, adapted matches 107:405; advance request over cap refused (error page with the API message), within cap persists and shows "à valider"; ADMIN validation persists after reload; non-ADMIN cannot validate (no action shown + 403 server side).
