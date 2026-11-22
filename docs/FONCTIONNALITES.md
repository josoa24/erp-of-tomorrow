# Fonctionnalités déjà présentes

Ce que l'ERP sait faire aujourd'hui, côté serveur. Ce qui a été ajouté et corrigé dans la branche `songomby_d2` est détaillé dans [BILAN_SONGOMBY_D2.md](BILAN_SONGOMBY_D2.md). Le détail de chaque endpoint est dans [API_ENDPOINTS.md](API_ENDPOINTS.md) ; ce qui reste à faire est dans [PLAN_API.md](PLAN_API.md), [PLAN_CRM.md](PLAN_CRM.md) et [PLAN_ERP.md](PLAN_ERP.md).

Interface : l'application web `novaui/` (React, Vite) propose l'écran de connexion et un premier tableau de bord. Les applications React Native (`apps/`) sont encore des squelettes vides.

## Accès et sécurité

- Connexion par email et mot de passe, qui renvoie un token JWT.
- Création et liste des comptes, réservées à `ADMIN` ; compte admin créé au premier démarrage.
- Changement de rôle, désactivation et réactivation d'un compte (`ADMIN`) ; un compte désactivé ne peut plus se connecter. Un admin ne peut pas modifier son propre compte.
- Chaque utilisateur peut changer son mot de passe (mot de passe actuel exigé).
- Rôles `ADMIN`, `STOCK`, `VENTE`, `RH`, `CRM` : la gateway refuse un module non autorisé (401 sans token, 403 sinon).
- La gateway transmet l'identité aux services (`X-User-Id`, `X-User-Email`, `X-User-Role`) ; chaque service refuse un appel direct qui ne passe pas par elle.
- CORS sur la gateway : les applications web peuvent l'appeler (origines réglables).
- Toutes les erreurs au même format, avec un `type` (`NOT_FOUND`, `VALIDATION`, `IMMUTABLE`, `BUSINESS`, `SERVER`, ...), y compris les erreurs inattendues.

## Stock

- Produits : création, modification, suppression (refusée si le produit a un historique), recherche par nom ou SKU.
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

- Départements et employés : gestion complète, recherche par nom ou département ; un employé peut être lié à son compte de connexion.
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

## Voix (`voice-service`)

- Transcription d'un message vocal en texte (Whisper, local et hors ligne) : `POST /api/voice/transcribe`.
- Transcription en temps réel par WebSocket (`/stream`), avec texte provisoire puis texte définitif.
- Français, malgache et anglais ; page de démonstration sur `http://localhost:8087/demo`.

## Outillage

- Swagger unique pour tous les services : `http://localhost:8080/swagger-ui.html`.
- Lancement complet par `docker compose` : une instance PostgreSQL (un schéma par service), services, gateway, `novaui` ; IA avec le profil `ai`, voix avec le profil `voice`. Environnement de développement avec rechargement à chaud : `docker-compose.dev.yml`.
- Intégration continue : tests des 6 modules Java, de l'ai-service, du voice-service et de `novaui` à chaque Pull Request.
- Tests unitaires et tests `@WebMvcTest` de chaque controller, dans chaque service (385 tests).
