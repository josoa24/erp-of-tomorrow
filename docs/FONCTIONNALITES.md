# Fonctionnalités déjà présentes

Ce que l'ERP sait faire aujourd'hui, côté serveur. Le détail de chaque endpoint est dans [API_ENDPOINTS.md](API_ENDPOINTS.md) ; ce qui reste à faire est dans [PLAN_API.md](PLAN_API.md), [PLAN_CRM.md](PLAN_CRM.md) et [PLAN_ERP.md](PLAN_ERP.md).

Les applications React Native (`apps/`) sont encore des squelettes vides : aucun écran n'existe. Seul le front web NovaUI (`novaui/`) a trois écrans (voir plus bas).

## Accès et sécurité

- Connexion par email et mot de passe, qui renvoie un token JWT.
- Création et liste des comptes, réservées à `ADMIN` ; compte admin créé au premier démarrage.
- Rôles `ADMIN`, `STOCK`, `VENTE`, `RH`, `CRM` : la gateway refuse un module non autorisé (401 sans token, 403 sinon).
- La gateway transmet l'identité aux services (`X-User-Id`, `X-User-Email`, `X-User-Role`).
- Erreurs au même format partout, avec un `type` (`NOT_FOUND`, `VALIDATION`, `IMMUTABLE`, `BUSINESS`, ...).

## Stock

- Produits : création, modification, suppression, recherche par nom ou SKU.
- Mouvements d'entrée et de sortie, avec refus d'une sortie supérieure au stock ; un mouvement ne se modifie pas.
- Produits en alerte (quantité sous le seuil).
- Autonomie d'un produit : ventes moyennes sur 7 jours, jours restants, risque `OK`, `WARNING`, `CRITICAL`.
- État de stock complet trié du plus urgent au moins urgent, et résumé (nombre de produits, alertes, valeur du stock).
- Fournisseurs : gestion complète, recherche, fiche 360 (commandes, total, délai moyen, produits fournis).
- Bons de commande : brouillon, validation (le bon devient immuable), réception qui crée les entrées de stock.

## Vente

- Clients : gestion complète, recherche, pagination, fiche 360 (total acheté, panier moyen, produits préférés).
- Ventes : brouillon modifiable, prix copié depuis le Stock, validation qui crée les sorties de stock.
- Si une ligne échoue à la validation, le stock déjà débité est restauré et la vente reste en brouillon.
- Chiffre d'affaires par période et par jour, produits les plus vendus.

## RH

- Départements et employés : gestion complète, recherche par nom ou département.
- Congés : demande en brouillon avec contrôle du solde et des chevauchements, validation ou refus définitifs.
- Pointage d'entrée et de sortie avec calcul de la durée travaillée.
- Résumé : effectif, effectif par département, personnes en congé aujourd'hui.

## CRM

- Historique des échanges avec un client ou un fournisseur (appel, email, réunion, visite, note), sans modification possible.
- Chronologie unifiée : échanges, ventes validées ou bons de commande d'un tiers.

## Assistant IA (`ai-service`)

- Discussion en langage naturel (Google Gemini), en réponse complète ou en flux continu.
- L'assistant interroge les modules avec le token de l'utilisateur : il n'a que les droits de ce dernier.
- Lecture : stock, autonomie, mouvements, fournisseurs, bons de commande, ventes, clients, chiffre d'affaires, RH, chronologie CRM.
- Écriture après confirmation explicite : mouvement de stock, bon de commande (création, validation, réception), vente (création, validation).
- Historique des conversations, page de test sur `/api/ai/ui`.

## Transcription vocale (`voice-service`, port 8087)

- Voix vers texte, en local et hors ligne (faster-whisper) : envoi d'un fichier audio (`POST /transcribe`, 10 Mo maximum par défaut).
- Transcription en temps réel par WebSocket (`/stream`) : texte provisoire pendant la phrase, texte final après 0,7 s de silence.
- Page de démonstration sur `/demo` (choix du micro, niveau sonore, journal de debug).
- Accessible via la gateway sur `/api/voice/**` (rôles `RH` et `ADMIN`). Pas encore relié à l'assistant IA.

## Front web NovaUI (`novaui/`)

- Connexion (e-mail et mot de passe, option « Se souvenir de moi »).
- Tableau de bord selon le rôle : chiffre d'affaires et tendance, disponibilité du stock, effectif et congés, points d'attention.
- Page d'erreur unique (réseau, accès refusé, session expirée).

## Outillage

- Swagger unique pour tous les services : `http://localhost:8080/swagger-ui.html`.
- Lancement complet par `docker compose` (bases PostgreSQL, services, gateway ; IA avec le profil `ai`).
- `docker-compose.dev.yml` : environnement de développement avec rechargement à chaud (Java, Python, NovaUI).
- Tests unitaires dans chaque service.
