# ADR-005 : l'IA derrière les API métier

Statut : acceptée.

## Contexte

Le thème du hackathon demande s'il faut combattre l'IA ou l'exploiter. Un assistant en langage naturel rend l'ERP plus simple à utiliser, mais un modèle de langage peut se tromper sur un chiffre, mal comprendre une demande ou être manipulé par un texte piégé. Il ne doit donc ni contourner les droits, ni écrire sans accord, ni inventer des chiffres.

## Décision

- **Service séparé** : `services/ai-service` en Python (FastAPI), port 8086, sans base. Il est routé par la gateway sur `/api/ai/**` comme les autres modules.
- **Gemini en function calling** sur une liste fermée d'outils (`app/tools/`). Chaque outil est un `ToolSpec` : déclaration, modèle d'arguments Pydantic (champs inconnus ignorés), fonction qui appelle un endpoint existant.
- **Outils filtrés par rôle** : `ToolRegistry.specs_for(role)` n'envoie à Gemini que les outils des modules du rôle (même règle que `RoleAccessPolicy`). Le rôle est revérifié à l'exécution de chaque outil.
- **Token de l'utilisateur** : l'ai-service reçoit le JWT de la requête et appelle la gateway avec lui (`ErpClient`). La gateway et les services appliquent les mêmes contrôles que pour un appel direct de l'utilisateur.
- **Confirmation humaine** pour toute écriture (`ActionGuard`) : l'outil d'écriture n'est pas exécuté, une action en attente est créée avec un récapitulatif. Elle n'est exécutée que par le bouton « Confirmer » (endpoint dédié, sans passer par le modèle) ou par `confirm_action` dans un tour postérieur à la proposition. Elle appartient à une conversation et à son auteur, expire (`CONFIRMATION_TTL_MIN`, 10 min par défaut) et ne s'exécute qu'une fois.
- **Les chiffres viennent des services** : stock, autonomie, chiffre d'affaires et fiches 360 sont calculés par les services Java. Le modèle choisit les outils et rédige la réponse ; le prompt lui interdit d'inventer des données.

## Conséquences

- L'IA n'a jamais plus de droits que l'utilisateur ; un rôle `STOCK` ne peut pas lire les ventes par l'assistant.
- Ajouter une capacité à l'IA, c'est ajouter un endpoint métier testé puis un outil qui l'appelle.
- Le choix du modèle est isolé dans l'ai-service ; Java n'en dépend pas.
- Les réponses dépendent d'un service externe (Gemini, `GEMINI_API_KEY`) : sans clé ou réseau, l'assistant est indisponible, le reste de l'ERP fonctionne.
- Conversations et actions en attente en mémoire vive : perdues au redémarrage, une seule instance.
- Le texte rédigé par le modèle n'est pas encore vérifié face aux chiffres renvoyés par les outils, et les actions de l'IA ne sont pas encore journalisées : ces deux points sont en cours sur d'autres branches.

## Alternatives écartées

- **Laisser l'IA générer du SQL ou lire les bases** : contourne les règles métier, l'immuabilité et les droits.
- **Clé technique avec tous les droits pour l'IA** : l'assistant verrait plus que l'utilisateur.
- **Écritures directes sans confirmation** : une erreur de compréhension deviendrait une écriture validée.
- **IA dans un service Java** : possible, mais l'écosystème Python (SDK `google-genai`, FastAPI, streaming) était plus rapide à mettre en place et l'équipe IA reste dans son dossier.
