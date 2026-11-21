# Plan ERP : ce qu'il reste pour un ERP complet

Ce plan complète [PLAN_API.md](PLAN_API.md) (modules existants) et [PLAN_CRM.md](PLAN_CRM.md) (clients et fournisseurs). Il suit le sujet du hackathon : un ERP où l'on parle ou l'on saisit une demande, où l'interface s'adapte à la réponse, où la saisie se fait par la voix ou un document, et qui anticipe les problèmes.

Statut : À faire. Taille : S (moins d'une demi-journée), M (une journée), L (plusieurs jours).

Chaque tâche suit [REGLES_DEVELOPPEMENT.md](REGLES_DEVELOPPEMENT.md).

---

## 1. Où on en est

| Domaine | Déjà fait | Manque |
|---------|-----------|--------|
| Stock | Produits, mouvements, autonomie, état de stock, fournisseurs, bons de commande | Entrepôts, lots, inventaire, valorisation, codes-barres |
| Vente | Clients, ventes, validation avec sortie de stock, rapports | Devis, livraison, retours, remises, paiements |
| RH | Départements, employés, congés, pointage, résumé | Contrats, paie, évaluations, recrutement |
| Auth | Comptes, rôles, connexion, contrôle par module | Refresh token, mot de passe, permissions fines |
| Finance | Rien | Factures, paiements, comptabilité, TVA, trésorerie |
| IA | Rien | Assistant, interface générée, saisie voix et document, alertes |
| Applications | Squelettes vides | Tous les écrans |
| Transverse | Gateway, tests unitaires | Audit, notifications, exports, CI, migrations de base |

## 2. Ordre de priorité

1. **Couche IA et applications** (phases A et B) : c'est le cœur du sujet et ce que le jury verra.
2. **Complétude métier** (phases C à G) : ventes, finance, achats, stock, RH.
3. **Fiabilité** (phase H) : sécurité, migrations, CI, déploiement.

Les quatre preuves à montrer en démonstration : une opération réelle (une phrase ou une facture modifie le stock après validation), une intelligence réelle (risque de rupture calculé et expliqué), une action réelle (un bon de commande préparé et validable), et le contrôle des droits par rôle.

---

## Phase A. Assistant IA (nouveau `ai-service`, port 8086)

Les calculs restent dans les services métier. L'IA comprend la demande, appelle les endpoints existants avec le token de l'utilisateur, et met en forme la réponse.

| # | Tâche | Détail | Taille | Dépend de | Statut |
|---|-------|--------|--------|-----------|--------|
| AI-1 | Squelette `ai-service` | Port 8086, route gateway `/api/ai`, clé du fournisseur IA par variable d'environnement (jamais commitée) | M | | À faire |
| AI-2 | Reconnaissance d'intention | Texte vers une intention et ses paramètres en JSON ; liste fermée d'intentions, jamais de code arbitraire | L | AI-1 | À faire |
| AI-3 | Orchestrateur | Appelle `/status`, `/summary`, `/sales/top-products`, etc. avec le token de l'utilisateur : ses droits sont respectés | L | AI-2 | À faire |
| AI-4 | Réponse avec interface | La réponse contient un message et une liste de composants (indicateur, tableau, graphique, bouton d'action) choisis dans un catalogue | M | AI-3 | À faire |
| AI-5 | Explication en langage naturel | Interpréter les résultats (« stock pour environ 2 jours ») ; les chiffres viennent des services | M | AI-3 | À faire |
| AI-6 | Saisie vocale | Voix vers texte, puis intention, puis opération préparée en brouillon, puis confirmation | L | AI-3 | À faire |
| AI-7 | Saisie par document | Photo de facture vers extraction (fournisseur, lignes, quantités, prix), puis bon de commande ou réception en brouillon | L | AI-3 | À faire |
| AI-8 | Contrôle des doublons et incohérences | Avant d'enregistrer : livraison déjà saisie, quantité anormale | M | AI-6, AI-7 | À faire |
| AI-9 | Actions proposées | « Prépare la commande nécessaire » crée un bon de commande en brouillon pour les produits `CRITICAL` ; confirmation explicite avant toute écriture | M | AI-3 | À faire |
| AI-10 | Simulation « et si » | « Si les ventes augmentent de 30 % » : ruptures prévues et budget de réapprovisionnement (endpoint de simulation dans Stock) | L | INV-7 | À faire |
| AI-11 | Alertes proactives | Tâche planifiée : ruptures, dépenses anormales, retards ; envoie des notifications | M | SYS-5 | À faire |
| AI-12 | Journal des actions IA | Qui a demandé quoi, ce qui a été proposé, ce qui a été confirmé | M | SYS-4 | À faire |
| AI-13 | Sécurité | Aucune écriture sans confirmation, limite de débit, pas de données personnelles envoyées inutilement, jeux de tests de demandes piégées | M | AI-3 | À faire |
| AI-14 | Compréhension du malgache | Demandes en malgache (priorité basse) | M | AI-2 | À faire |

## Phase B. Applications React Native

| # | Tâche | Détail | Taille | Dépend de | Statut |
|---|-------|--------|--------|-----------|--------|
| APP-1 | Bibliothèque de composants communs | Bouton, champ, liste, carte, indicateur, page d'erreur ; à réutiliser dans les trois applications | M | | À faire |
| APP-2 | Connexion et stockage du token | Écran de connexion, déconnexion, redirection selon le rôle | M | APP-1 | À faire |
| APP-3 | Page d'erreur dédiée | Types `NOT_FOUND`, `VALIDATION`, `BUSINESS`, `IMMUTABLE`, `UNAUTHORIZED`, `FORBIDDEN`, `SERVER` | S | APP-1 | À faire |
| APP-4 | Écrans Stock | Produits, fiche produit, mouvements, état de stock, fournisseurs, bons de commande | L | APP-2 | À faire |
| APP-5 | Écrans Vente | Clients, ventes, validation, chiffre d'affaires, meilleurs produits | L | APP-2 | À faire |
| APP-6 | Écrans RH | Départements, employés, congés, pointage, résumé | L | APP-2 | À faire |
| APP-7 | Écran de discussion | Zone de saisie, réponses avec composants générés par l'IA | L | AI-4 | À faire |
| APP-8 | Saisie vocale | Enregistrement et envoi à `ai-service` | M | AI-6 | À faire |
| APP-9 | Scan de document | Caméra, envoi de la photo, vérification avant validation | M | AI-7 | À faire |

## Phase C. Ventes avancées (`vente-service`)

| # | Tâche | Détail | Taille | Dépend de | Statut |
|---|-------|--------|--------|-----------|--------|
| SAL-1 | Devis | Brouillon, envoi, acceptation ou refus, conversion en vente | M | | À faire |
| SAL-2 | Bon de livraison | Livraison partielle ou totale d'une vente validée | M | | À faire |
| SAL-3 | Retours et avoirs | Retour d'un produit : entrée de stock et avoir ; les ventes validées ne sont jamais modifiées | L | | À faire |
| SAL-4 | Remises et promotions | Remise par ligne ou par vente, avec plafond selon le rôle | M | | À faire |
| SAL-5 | Listes de prix | Prix par client ou par groupe de clients | M | | À faire |
| SAL-6 | Mode de paiement | Espèces, mobile money, virement | S | FIN-2 | À faire |

## Phase D. Facturation et comptabilité (nouveau `finance-service`, port 8087)

| # | Tâche | Détail | Taille | Dépend de | Statut |
|---|-------|--------|--------|-----------|--------|
| FIN-1 | Factures clients | Créées depuis une vente validée, numérotation continue, immuables | L | | À faire |
| FIN-2 | Paiements clients | Paiements partiels, reste à payer, surpaiement refusé | M | FIN-1 | À faire |
| FIN-3 | Factures fournisseurs | Créées depuis une réception de bon de commande | M | | À faire |
| FIN-4 | Paiements fournisseurs | Paiements partiels et reste à payer | M | FIN-3 | À faire |
| FIN-5 | Plan comptable simplifié | Comptes de base et paramétrage | M | | À faire |
| FIN-6 | Écritures automatiques | Vente, achat et paiement génèrent leurs écritures | L | FIN-5 | À faire |
| FIN-7 | TVA | Taux, calcul et déclaration | M | FIN-6 | À faire |
| FIN-8 | États comptables | Journal, grand livre, balance | L | FIN-6 | À faire |
| FIN-9 | Clôture de période | Une période close devient immuable | M | FIN-8 | À faire |
| FIN-10 | Trésorerie | Prévision des entrées et sorties à partir des factures et paiements | M | FIN-2, FIN-4 | À faire |
| FIN-11 | Export comptable | CSV ou Excel | S | FIN-8 | À faire |

## Phase E. Achats (`stock-service`)

| # | Tâche | Détail | Taille | Dépend de | Statut |
|---|-------|--------|--------|-----------|--------|
| ACH-1 | Demandes d'achat | Demande interne validée avant le bon de commande | M | | À faire |
| ACH-2 | Prix fournisseurs par produit | Historique des prix | M | | À faire |
| ACH-3 | Comparaison de fournisseurs | Meilleur prix et meilleur délai pour un produit | M | ACH-2 | À faire |
| ACH-4 | Réapprovisionnement suggéré | Liste de bons de commande proposés à partir de `/status` | M | | À faire |
| ACH-5 | Réception partielle | Plusieurs réceptions pour un même bon | M | | À faire |
| ACH-6 | Retour fournisseur | Sortie de stock et avoir fournisseur | M | FIN-3 | À faire |

## Phase F. Stock avancé (`stock-service`)

| # | Tâche | Détail | Taille | Dépend de | Statut |
|---|-------|--------|--------|-----------|--------|
| INV-1 | Entrepôts et emplacements | Stock par entrepôt | L | | À faire |
| INV-2 | Transferts entre entrepôts | Deux mouvements liés | M | INV-1 | À faire |
| INV-3 | Inventaire physique | Comptage, écarts, ajustement avec motif ; un inventaire validé est immuable | L | | À faire |
| INV-4 | Lots et péremption | Numéro de lot, date limite, sortie par date la plus proche | L | | À faire |
| INV-5 | Codes-barres | Recherche d'un produit par code | M | | À faire |
| INV-6 | Catégories et unités | Classement des produits, conversion d'unités | M | | À faire |
| INV-7 | Seuil minimum dynamique et simulation | Seuil calculé selon les ventes ; endpoint de simulation d'une hausse de la demande | L | | À faire |
| INV-8 | Valorisation | Coût moyen pondéré ou FIFO | L | | À faire |

## Phase G. RH avancé (`rh-service`)

| # | Tâche | Détail | Taille | Dépend de | Statut |
|---|-------|--------|--------|-----------|--------|
| HRX-1 | Contrats | Type, dates, salaire ; historique conservé | M | | À faire |
| HRX-2 | Paie | Salaire de base, primes, retenues, bulletin ; un bulletin validé est immuable | L | HRX-1 | À faire |
| HRX-3 | Types de congés et jours fériés | Maladie, maternité, jours fériés exclus du décompte | M | | À faire |
| HRX-4 | Heures et paie | Heures travaillées issues du pointage, heures supplémentaires | M | HRX-2 | À faire |
| HRX-5 | Hiérarchie et approbation | Un manager valide les congés de son équipe | M | | À faire |
| HRX-6 | Lien compte et employé | `userId` sur la fiche employé (A9 de [PLAN_API.md](PLAN_API.md)) | S | | À faire |
| HRX-7 | Évaluations | Campagnes et résultats | M | | À faire |
| HRX-8 | Recrutement | Offres, candidatures, étapes | L | | À faire |

## Phase H. Transverse

| # | Tâche | Détail | Taille | Dépend de | Statut |
|---|-------|--------|--------|-----------|--------|
| SYS-1 | Permissions fines | Droits par action, pas seulement par module | L | | À faire |
| SYS-2 | Sécurité des comptes | Renouvellement de token, déconnexion, changement de mot de passe (A6 à A8) | M | | À faire |
| SYS-3 | Services joignables uniquement par la gateway | En-têtes `X-User-*` obligatoires (A10) | M | | À faire |
| SYS-4 | Journal d'audit | Qui a fait quoi et quand, dans tous les services | L | | À faire |
| SYS-5 | Notifications | Dans l'application, par email ou push | L | | À faire |
| SYS-6 | Rapports et exports | PDF, CSV, Excel | M | | À faire |
| SYS-7 | Import | CSV de produits et de clients | M | | À faire |
| SYS-8 | Devise et arrondis | Ariary, arrondis cohérents | S | | À faire |
| SYS-9 | Paramètres de la société | Nom, logo, numérotation, TVA | S | | À faire |
| SYS-10 | Pagination et tri partout | Format commun pour toutes les listes | M | | À faire |
| SYS-11 | Intégration continue | Build et tests à chaque Pull Request | M | | À faire |
| SYS-12 | Déploiement | Un Dockerfile par service, `docker-compose` complet | M | | À faire |
| SYS-13 | Observabilité | Logs structurés, santé, métriques | M | | À faire |
| SYS-14 | Migrations de base | Remplacer `ddl-auto: update` par des migrations versionnées (Flyway) | L | | À faire |
| SYS-15 | Tests sur PostgreSQL | Tests d'intégration avec Testcontainers (aujourd'hui testé sur H2 seulement) | L | | À faire |
| SYS-16 | Documentation des API | OpenAPI / Swagger pour chaque service | M | | À faire |
| SYS-17 | Sauvegarde et restauration | Procédure et test de restauration | M | | À faire |

---

## Version minimale pour la démonstration du hackathon

Scénario en quatre scènes, avec ce qui existe déjà et ce qu'il reste à faire :

| Scène | Ce qui existe | À faire |
|-------|---------------|---------|
| Une facture devient une opération | Bons de commande, validation, réception | AI-1, AI-2, AI-7, APP-9 |
| Une phrase devient une vente | Ventes et validation avec sortie de stock | AI-6, APP-8 |
| Une question devient une décision | `GET /status` (risque par produit) | AI-3, AI-4, AI-5, APP-7 |
| Une recommandation devient une action | Bons de commande en brouillon | AI-9 |

Complément indispensable : APP-1 à APP-3 (composants, connexion, page d'erreur) pour que tout soit utilisable.

## Questions à trancher

1. Quel fournisseur d'IA et quel budget pour les appels (texte, voix, lecture de documents) ?
2. La finance (phase D) est-elle dans le périmètre du hackathon, ou seulement dans la vision future ?
3. Une bibliothèque de composants partagée entre applications (`packages/ui`) ou une copie par application ?
4. Combien de temps reste-t-il pour le prototype (5 h, 24 h, 48 h) ? Cela détermine les phases à garder.
