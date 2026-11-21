# Décisions d'architecture (ADR)

Un ADR (Architecture Decision Record) note une décision importante : le contexte, ce qui a été décidé, ce que cela implique, et les alternatives écartées. Ils complètent [ARCHITECTURE.md](../ARCHITECTURE.md).

| N° | Décision | Statut |
|----|----------|--------|
| [ADR-001](ADR-001-microservices-base-par-service.md) | Microservices Spring Boot, une base par service, une gateway unique | Acceptée |
| [ADR-002](ADR-002-jwt-gateway-en-tetes-identite.md) | JWT vérifié à la gateway, identité propagée par en-têtes `X-User-*` | Acceptée |
| [ADR-003](ADR-003-separation-reader-creator-updater-deleter.md) | Séparation Reader / Creator / Updater / Deleter, interfaces, DTO, 100 lignes par fichier | Acceptée |
| [ADR-004](ADR-004-donnees-validees-immuables.md) | Données validées immuables, correction par nouvelle écriture | Acceptée |
| [ADR-005](ADR-005-ia-derriere-les-api-metier.md) | IA derrière les API métier, avec les droits de l'utilisateur et confirmation humaine | Acceptée |

Pour une nouvelle décision : copier un ADR existant, prendre le numéro suivant, garder les quatre parties (Contexte, Décision, Conséquences, Alternatives écartées) et l'ajouter à ce tableau.
