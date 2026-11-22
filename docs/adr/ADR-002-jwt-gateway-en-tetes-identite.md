# ADR-002 : JWT vérifié à la gateway, identité propagée par en-têtes `X-User-*`

Statut : acceptée.

## Contexte

Chaque requête doit être authentifiée et limitée au module du rôle de l'utilisateur. Répéter la vérification du token et des rôles dans six services (cinq en Java, un en Python) multiplierait le code de sécurité et les risques d'écart entre services.

## Décision

- L'auth-service vérifie l'email et le mot de passe (BCrypt, compte actif) et émet un JWT signé HMAC avec `JWT_SECRET` : `sub` = email, `userId`, `role` (rôle principal), `roles` (tous les rôles du compte), expiration `JWT_TTL_MINUTES` (480 par défaut).
- La gateway (`GatewayAuthFilter`, exécuté en premier) vérifie le token avec le même secret (`JwtTokenParser`), puis le rôle avec `RoleAccessPolicy` : un accès accordé dès qu'un des rôles du compte donne le module (`roles`, ou `[role]` pour un ancien token), `ADMIN` partout, `/api/ai` ouvert à tous les rôles. Sans token valide : 401 `UNAUTHORIZED`. Rôle non autorisé : 403 `FORBIDDEN`.
- La gateway supprime les en-têtes `X-User-Id`, `X-User-Email`, `X-User-Role` et `X-User-Roles` envoyés par le client, puis les réécrit à partir du token : `X-User-Role` = rôle principal (ordre ADMIN, STOCK, VENTE, RH, CRM), `X-User-Roles` = tous les rôles séparés par des virgules.
- Chaque service Java a un `GatewayIdentityFilter` qui refuse (401) toute requête sans `X-User-Id` et `X-User-Role` : un appel direct qui contourne la gateway est rejeté. Restent ouverts : Swagger, `/actuator`, et `/login` pour l'auth-service.
- Quand un service en appelle un autre (vente vers stock, crm vers vente et stock), `IdentityForwarding` recopie les en-têtes `X-User-*` de la requête d'origine.
- Chemins publics : `POST /api/auth/login`, `GET /api/ai/ui`, les définitions OpenAPI.

## Conséquences

- La règle d'accès est à un seul endroit (`RoleAccessPolicy`) et testée (tests de la gateway).
- Les services restent simples : ils lisent l'identité dans les en-têtes, sans dépendance JWT.
- Le contrôle est par module, pas par action : dans son module, un rôle peut tout faire (tâche SYS-1).
- **Limite** : les services font confiance aux en-têtes. Si le port d'un service est joignable, on peut les imiter. Les ports des services ne sont donc pas publiés ; en production il faudra un réseau privé ou un secret partagé entre gateway et services.
- **Limite** : pas de renouvellement ni de révocation de token. Un compte désactivé garde un token valable jusqu'à son expiration (tâche SYS-2).

## Alternatives écartées

- **Vérifier le JWT dans chaque service** : plus robuste face à un port exposé, mais duplique la logique dans chaque service et en Python.
- **Sessions côté serveur** : demande un stockage partagé des sessions entre services.
- **Serveur OAuth2 / OpenID Connect externe** (Keycloak...) : plus complet (refresh token, révocation), mais lourd à installer et configurer pour le hackathon.
