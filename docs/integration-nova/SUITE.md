# Intégration Figma NOVA — état au 22/11/2026 et suite sur une autre machine

Ce document permet de reprendre l'intégration des écrans Figma NOVA (sections 03 Accueil & Navigation, 04 Eclipse AI, 05 Stock, 06 Ventes & CRM, 07 Ressources humaines) là où elle s'est arrêtée. Le travail a été interrompu volontairement, faute de puissance sur la machine d'origine (4 cœurs). Tout ce qui existait a été commité, puis fusionné dans `songomby_d2`.

## 1. En bref

- Les branches d'intégration ont été fusionnées dans `songomby_d2`, via la branche d'assemblage `integration/nova-merge`, sans conflit textuel. Quatre conflits de types (paramètres d'URL et `staticData` des routes) et trois erreurs de lint (espaces insécables) apparus après la fusion ont été corrigés dans le commit `fix(integration)`.
- Chaque branche se termine par un commit `chore(<scope>): sauvegarder le travail en cours avant changement de machine (inachevé…)`. Ces commits contiennent du travail **non relu** : il faut les relire en priorité.
- Le backend est très avancé : moteur de modules, Eclipse (ai-service), Stock, RH, Auth, et Vente en travail en cours. Le front a le socle complet et les écrans Stock et Relations en version générique. Les écrans Ventes, Équipe, Mon profil, Accueil et Livraisons, ainsi que les parcours Eclipse côté front, restent à terminer.
- Aucune vérification de bout en bout sur la vraie pile, aucune capture de responsive sur la version fusionnée, aucun jeu de données de démonstration : ce sont les prochaines étapes.

## 2. Décisions déjà prises (à respecter)

1. **Adaptations « après demande Eclipse » = vraies fonctions métier** dans le service propriétaire : lot et péremption avec alerte J-30 (stock), Mobile Money et crédit (vente), NIF/STAT et plafond de crédit (clients), avances plafonnées à 30 % du salaire (RH). Eclipse reconnaît la demande dans un catalogue fermé de 4 adaptations, affiche l'aperçu, et « Appliquer pour l'équipe » enregistre l'activation. Une demande hors catalogue reçoit une réponse honnête.
2. **« Créer un module » = moteur de modules générique** (`services/module-service`, schéma `module`, route gateway `/api/modules/**`). Livraisons est la première instance ; Réparations et Rendez-vous doivent aussi fonctionner.
3. **Droits** : tout le monde peut demander et voir l'aperçu. Appliquer une adaptation et créer ou supprimer un module exigent le rôle ADMIN (gérant), contrôlé côté serveur.
4. **Décisions de contrat** prises pendant l'intégration (détail dans `registry.md`, section « Décisions et écarts ») :
   - D1 : `CREATE_MODULE` devient un résultat possible de `POST /api/ai/eclipse/adaptations/propose`.
   - D2 : les colonnes de module ont un booléen `listed`. Il est **à faire** (suivi F-I6, F-I7).
   - D3 : la validation des définitions côté ai-service doit être aussi stricte que celle du module-service. **À faire** (F-I7).
   - Changement de propriétaire : `useModuleAdaptation`, `EclipseAdaptationProvider`, les constantes d'adaptation et le type `AdaptationProposal` appartiennent au socle (registre R3b).
5. **Git** : branche par intégration `<module>/<type>-<description>`. Commits au format `type(scope): action précise`, au nom du développeur. **Jamais** de ligne `Co-Authored-By` ni de mention d'IA. Aucune fusion dans `songomby_d2` sans validation explicite d'un SHA par le responsable.
6. **Code** : 100 lignes au maximum par fichier, **aucun commentaire dans le code**, séparation des couches API, services, hooks et composants, erreurs sur la page d'erreur dédiée, données validées immuables, tests pour toute nouvelle logique (voir `common-rules.md`).

## 3. Fichiers de référence (dans ce dossier)

| Fichier | Contenu |
|---|---|
| `registry.md`, `registry-part-modules-eclipse.md` | Registre des éléments partagés : contrats d'API exacts (S1–S3, V1–V6, P1–P3, H1–H3, A1, C1–C3), propriétaires, consommateurs, décisions D1–D3 |
| `common-rules.md` | Règles communes à tout intégrateur : git, code, base de données, outillage, vérification visuelle |
| `briefs/I0…I7*.md` | Brief détaillé de chaque intégration (périmètre, phase A backend, phase B front, critères d'acceptation) |
| `briefs/F-I6-listed.md`, `briefs/F-I7-align.md` | Suivis à faire pour D2 et D3 |
| `briefs/R11-demo-data.md` | Jeu de données de démonstration à créer |
| `analyses/*.md` | Analyses initiales : front, design system Figma, stock et vente, RH, CRM et auth, ai-service |
| `workflows/*.js`, `workflows/make-ui-worktrees.sh` | Scripts d'orchestration Claude Code : intégration, phase, tâche rapide, coordination, vérification globale |

Les briefs mentionnent `<SCRATCH>` : remplacer par un dossier de travail temporaire de la nouvelle machine. Les chemins de worktree `/home/saandria/Documents/devfest/erp-of-tomorrow.worktrees/...` sont propres à l'ancienne machine : les recréer (section 7) ou les adapter. Dans les scripts `workflows/*.js`, adapter les constantes `RULES` et `B` au chemin du dépôt.

Figma : fichier `Zdv2NuVCiDtDrlAQA0vyh0`, page « 18 — Proposition · Socle + IA (flow) ».

| Section | Frames |
|---|---|
| Connexion | 130:3574, et ses états 130:3607, 130:3636, 130:3669, 130:3697 (mobile 390) |
| Profil | menu 138:4106, Mon profil 138:4137, rôle Ventes 138:4405 |
| Accueil | 80:46 |
| Eclipse | 135:3634, 135:4406, 135:4481, 135:4084, 135:3787 |
| Stock | 107:375, 107:385 |
| Ventes | 107:355, 107:365 |
| Relations | 107:415, 107:425 |
| Équipe | 107:395, 107:405 |

## 4. Branches fusionnées et état réel

L'ordre de fusion dans `songomby_d2` est celui du tableau. « Relu » signifie qu'une relecture indépendante a été faite ; « en cours » signifie qu'un commit de sauvegarde inachevé termine la branche.

| Intégration | Branche (dernier commit) | Backend | Front | Reste à faire |
|---|---|---|---|---|
| I0 Socle front | `web/feat-nova-socle` (8b242b8) | — | Jetons NOVA, marque, primitives, shell (nav par rôle, menu du compte, barre mobile), gabarit ModulePage/DataTable, EclipseBar, routes protégées par rôle, client HTTP. 15 commits + travail en cours | Relecture complète. Finir le travail en cours (`dockPlaceholder`). Vérification visuelle à 375/768/1440 |
| I0b État des adaptations | `web/feat-nova-adaptation-state` (31ba3dc) | — | Catalogue, API des activations, provider avec synchronisation `?eclipse=`, `useModuleAdaptation` (3 commits) + en cours (`ShellBottomArea`, `useRouteAdaptationModule`) | Terminer la bascule dock Eclipse ↔ AdaptationBar dans le shell. Tests. `staticData.adaptationModule` est déjà déclaré dans le commit de fusion |
| I6 Moteur de modules | `modules/feat-module-engine` (e942941) | module-service complet : définitions, fiches, références, indicateurs, import des ventes, adaptations, gateway, CI, docs. 11 commits + en cours (`apply-migrations.sh`, migrations 100/101) | — | Relecture. Suivi F-I6 (`listed`). Test sur vraie pile (compose, migrations appliquées 2 fois) |
| I7 Eclipse backend | `eclipse/feat-eclipse-flows` (463b4b0) | `/api/ai/eclipse` : propositions d'adaptation et de module, suggestions, Gemini optionnel. Relu, 2 commits de correction + en cours | — | Suivi F-I7 (D1–D3). Finir le test `test_eclipse_matcher_vocabulary.py` |
| I7 Eclipse front | `eclipse/feat-eclipse-flows-ui` (a2ce393) | — | Port du flux SSE (analyseur, événements, réducteurs, API) + en cours (`eclipseAvailability`) | **Presque tout** : `useEclipseRequest`, contenu de l'EclipseDock, panneau de réponse, fenêtres 04a/04b, toast 04c, écran de module générique 04c/04d, `EclipseHeroBar` |
| I2 Stock | `stock/feat-nova-stock` (9bf7f10) + `stock/feat-nova-stock-ui` (7118999) | S1–S3 (catégorie, prix d'achat, statuts, lots FEFO, péremption) + en cours (compteurs `/status/counts`) | Écran générique en cours (API, composants, hooks, services, types) | Relecture backend. Finir l'écran, la fenêtre d'entrée de stock avec lot, et le branchement de l'adaptation via `useModuleAdaptation('stock')` |
| I3 Ventes | `vente/feat-nova-ventes` (f6657c4) + `vente/feat-nova-ventes-ui` (83ccb1e) | V1–V6 en **un seul commit en cours** (compile ; 181 tests passent) | Types, API, services (filtres, indicateurs, encaissement) + hooks en cours ; écran encore en attente | Relire et découper si besoin le commit backend en cours. Écran Ventes, fenêtres Nouvelle vente, Encaisser et Annuler, adaptation Mobile Money/crédit |
| I4 Relations | `crm/feat-nova-relations` (3feca54) + `crm/feat-nova-relations-ui` (d32f94e) | P1 (clients) et P2 (fournisseurs) faits ; P3 (contacts CRM agrégés) en cours | Écran générique en cours (vue générique, panneaux Nouveau contact et Échange) | Terminer P3 et ses tests. Terminer l'écran. Brancher l'adaptation NIF/STAT/plafond |
| I5 Équipe | `rh/feat-nova-equipe` (7f8d87e) + `rh/feat-nova-equipe-ui` (a31aa63) | H1–H3 (contrat, congés du jour, décideur, avances ≤ 30 % validées par ADMIN) + en cours (compteurs, statut) | Types, API, services + hooks en cours ; écran encore en attente | Relecture backend. Écran Équipe, fenêtres (employé, demandes de congé, avance), adaptation avances |
| I1 Accueil & navigation | `accueil/feat-nova-accueil-navigation` (a3bdc21) + `accueil/feat-nova-accueil-navigation-ui` (4b11595) | A1 (nom complet, `GET /users/me`, `ADMIN_NAME`) fait ; relecture lancée mais pas de passe de correction | Mémorisation de l'e-mail après refus + en cours | Connexion (états 00 à 00d), page d'erreur (00c, 8 types), données du menu du compte, Mon profil (00f/00g), Accueil (01) avec barre Eclipse, indicateurs par rôle et liste « À décider » |

## 5. Étapes restantes, dans l'ordre

1. **Préparer la nouvelle machine** (section 7) : cloner, `git checkout songomby_d2`, installer Node 22+, Docker et Python 3.13 (JDK 17 et Maven facultatifs si l'on passe par Docker). Lancer les contrôles de la section 6 pour repartir d'un état connu.
2. **Relire les commits « sauvegarder le travail en cours »** de chaque branche (`git log --grep "travail en cours"`). Les compléter ou les corriger en petits commits propres. Les fichiers de plus de 100 lignes et les commentaires sont interdits.
3. **Terminer le socle** (I0 et I0b), puis le relire : c'est la base de tous les écrans.
4. **Suivis de contrat** : F-I6 (`listed` dans module-service, migration 102) et F-I7 (alignement ai-service, D1–D3).
5. **Écrans métier**, chacun selon son brief (phase B), en réutilisant le gabarit ModulePage et `useModuleAdaptation` :
   - Stock : finir et brancher l'adaptation.
   - Ventes : écran et fenêtres.
   - Équipe : écran et fenêtres.
   - Relations : finir P3 et l'écran.
6. **Eclipse front** (I7 phase B) : barre et réponses, aperçu et application des adaptations, création de module (04a et 04b), écran de module générique (Livraisons, Réparations, Rendez-vous), import « Reprendre les N ventes ».
7. **Accueil et navigation** (I1 phase B) : Connexion, page d'erreur, Mon profil, Accueil avec `EclipseHeroBar`.
8. **Passe de coordination** (`workflows/coordinate.workflow.js`) : vérifier que chaque producteur et chaque consommateur respecte le registre (noms de champs, statuts, rôles).
9. **Données de démonstration** : `scripts/demo/` existe déjà sur `songomby_d2`. Un collègue l'a ajouté (PR #38) : `seed_demo.py`, `seed_core.py`, `seed_more.py`, et les vérifications `verify_*.py`. **Compléter ces scripts**, ne pas en créer d'autres. Il faut y ajouter les nouveaux champs : catégories, prix d'achat, lots et péremptions, encaissements, crédits, à livrer, NIF/STAT et plafonds, contrats, avances, nom complet des comptes. Suivre `briefs/R11-demo-data.md` pour le contenu attendu. Pour l'historique, étendre le `backdate.py` existant.
10. **Vérification globale** (`workflows/verify.workflow.js`) sur la vraie pile, aux largeurs 375, 768 et 1440 :
    - Pour chaque écriture, recharger et vérifier la persistance.
    - Pour chaque lecture, comparer les totaux et compteurs avec l'API.
    - Tester les rôles ADMIN et VENTE.
    - Tester les 4 phrases Figma : aperçu, Annuler, puis Appliquer.
    - Création de Livraisons depuis le « + ».
11. **Relecture finale et demande de validation** du SHA de `songomby_d2` avant toute fusion vers `main`.

## 6. Contrôles au moment de la fusion (état de `songomby_d2`)

Tous les tests ont tourné sur l'arbre fusionné. Les tests Java et Python sont passés avant le commit `fix(integration)`, qui ne touche que le front.

| Contrôle | Commande | Résultat |
|---|---|---|
| Front : lint | `cd novaui && npm ci && npm run lint` | OK : 0 erreur, après le commit `fix(integration)` (3 espaces insécables bruts échappés) |
| Front : types | `npx tsc -b` | OK, après le commit `fix(integration)` (4 erreurs de types de routes corrigées) |
| Front : tests | `npx vitest run` | OK : 141 fichiers, 628 tests ; le test des écrans d'attente qui visait encore Stock a été adapté |
| Front : build | `npx vite build` | OK (PWA générée) |
| ai-service | `GEMINI_API_KEY= AI_AUDIT_DB=:memory: python -m pytest -q` | OK : 228 tests |
| module-service | `mvn -B test` | OK : 197 tests |
| stock-service | `mvn -B test` | OK : 279 tests |
| vente-service | `mvn -B test` | OK : 181 tests |
| rh-service | `mvn -B test` | OK : 149 tests |
| crm-service | `mvn -B test` | OK : 115 tests |
| auth-service | `mvn -B test` | OK : 58 tests |
| gateway | `mvn -B test` | OK : 37 tests |

Sans Java local, lancer Maven via Docker depuis le dossier du service :

```bash
docker run --rm -v "$PWD":/w -v "$HOME/.m2-docker":/root/.m2 -w /w maven:3.9-eclipse-temurin-17 mvn -B test
```

Docker crée `target/` en root : le supprimer avec `docker run --rm -v "$PWD":/w alpine rm -rf /w/target`.

## 7. Lancer le projet et appliquer les migrations en local

```bash
git checkout songomby_d2
docker compose build
docker compose up -d erp-db
./docker/postgres/apply-migrations.sh
docker compose up -d
docker compose --profile ai up -d ai-service
cd novaui && npm ci && npm run dev
```

- **Gateway :** http://localhost:8080. Swagger : `/swagger-ui.html`. Compte admin : `ADMIN_EMAIL` / `ADMIN_PASSWORD`, et `ADMIN_NAME` pour le nom affiché.
- **Nouveau service :** `module-service` (port 8088, schéma `module`, utilisateur `module_svc`). Sur un **nouveau** volume, `docker/postgres/init/01-schemas.sh` crée le schéma. Sur un volume **existant**, c'est la migration `100_module_schema.sql` qui le crée, via `apply-migrations.sh`.
- **Sans `GEMINI_API_KEY` :** la reconnaissance des 4 adaptations et des modèles de module fonctionne quand même (matcher déterministe). Les questions libres renvoient « Eclipse indisponible » (503).
- **Migrations** (idempotentes, à appliquer dans l'ordre des numéros, deux passages sans erreur attendus) :

| Numéro | Objet |
|---|---|
| 100, 101 | Schéma et tables du module-service |
| 200 | Stock : catégorie et prix d'achat |
| 201 | Stock : lots et allocations FEFO (FK vers products et stock_movements) |
| 300 | Vente : colonnes paiement, crédit et à livrer |
| 301 | Vente : `sale_payments`, avec rattrapage d'un encaissement en espèces pour les ventes validées existantes |
| 302 | Vente : `sale_cancellations` |
| 400 | Vente : clients NIF, STAT et plafond de crédit |
| 401 | Stock : fournisseurs ville, NIF et STAT |
| 500 | RH : type de contrat |
| 501 | RH : décideur des congés |
| 502 | RH : avances sur salaire |
| 600 | Auth : nom complet |

  Chaque migration est à relire avant application sur des données réelles.
- **Recréer les worktrees** d'intégration pour continuer branche par branche, par exemple :

```bash
git worktree add ../wt-stock stock/feat-nova-stock
git -C ../wt-stock merge --no-edit songomby_d2
```

## 8. Points de vigilance

- Les commits « travail en cours » ont été pris tels quels à l'arrêt des agents. Certains fichiers peuvent être incomplets alors que les contrôles passent : vérifier le comportement, pas seulement les tests.
- Hibernate (`ddl-auto: update`) ne met pas à jour les contraintes CHECK des enums : toute nouvelle valeur d'enum demande une migration qui recrée la contrainte.
- Fuseau `Indian/Antananarivo` pour « aujourd'hui », « cette semaine » et « ce mois ». Plusieurs anciens calculs étaient en UTC.
- Le rôle VENTE n'a pas accès à `/api/stock` : la saisie de vente passe par le catalogue relayé `GET /api/vente/catalog/products` (V6).
- Les références d'un module vers client, vente ou employé sont des liens logiques entre schémas (pas de FK physique) : leur existence est vérifiée par l'API du service propriétaire.
- Les volumes du Figma (64 produits, 412 ventes) n'existent que via les scripts de `scripts/demo/`, complétés selon R11. Le scénario de démonstration du pitch est documenté par le collègue (PR #39).
- La pile de développement `erp-dev` de l'ancienne machine a été arrêtée sans être supprimée. Rien à faire sur la nouvelle machine.
