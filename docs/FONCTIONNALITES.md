# Fonctionnalités déjà présentes

Ce que l'ERP sait faire aujourd'hui, côté serveur. Ce qui a été ajouté et corrigé dans la branche `songomby_d2` est détaillé dans [BILAN_SONGOMBY_D2.md](BILAN_SONGOMBY_D2.md). Le détail de chaque endpoint est dans [API_ENDPOINTS.md](API_ENDPOINTS.md) ; ce qui reste à faire est dans [PLAN_API.md](PLAN_API.md), [PLAN_CRM.md](PLAN_CRM.md) et [PLAN_ERP.md](PLAN_ERP.md).

Il n'y a qu'une seule application web, NovaUI (`novaui/`), pour tous les modules ; elle a trois écrans pour l'instant (voir plus bas). Les écrans de chaque module iront dans `novaui/src/features/<module>/`.

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

## Transcription vocale (`voice-service`, port 8087)

- Voix vers texte, en local et hors ligne (faster-whisper) : envoi d'un fichier audio (`POST /transcribe`, 10 Mo maximum par défaut).
- Transcription en temps réel par WebSocket (`/stream`) : texte provisoire pendant la phrase, texte final après 0,7 s de silence.
- Page de démonstration sur `/demo` (choix du micro, niveau sonore, journal de debug).
- Accessible via la gateway sur `/api/voice/**` (rôles `STOCK`, `VENTE`, `RH`, `CRM` et `ADMIN`). Son port n'est pas publié : seule la gateway est exposée.
- Relié à Eclipse dans NovaUI : le bouton micro de la barre Eclipse (raccourci `Alt M`) enregistre la voix, l'envoie à `POST /api/voice/transcribe` puis remplit la barre ou envoie la demande à l'assistant. États gérés : permission, micro absent ou occupé, silence, enregistrement trop long, service indisponible. Guide de test : [GUIDE_TEST_VOCAL.md](GUIDE_TEST_VOCAL.md).

## Front web NovaUI (`novaui/`)

- Connexion (e-mail et mot de passe, option « Se souvenir de moi »).
- Tableau de bord selon le rôle : chiffre d'affaires et tendance, disponibilité du stock, effectif et congés, points d'attention.
- Page d'erreur unique (réseau, accès refusé, session expirée).

## Outillage

- Swagger unique pour tous les services : `http://localhost:8080/swagger-ui.html`.
- Lancement complet par `docker compose` (base PostgreSQL unique avec un schéma par service, services, gateway ; IA avec le profil `ai`).
- `docker-compose.dev.yml` : environnement de développement avec rechargement à chaud (Java, Python, NovaUI).
- Tests unitaires et tests `@WebMvcTest` de chaque controller, dans chaque service (385 tests).
