# ADR-003 : séparation Reader / Creator / Updater / Deleter, interfaces, DTO, 100 lignes par fichier

Statut : acceptée.

## Contexte

Plusieurs équipes, et du code en partie écrit avec une IA, doivent produire un code facile à relire en Pull Request. Un gros `ProductService` qui fait tout devient vite long, difficile à tester et source de conflits Git.

## Décision

Règles détaillées dans [REGLES_DEVELOPPEMENT.md](../REGLES_DEVELOPPEMENT.md) :

- Un package par entité : `controller`, `service/{reader,creator,updater,deleter}`, `repository`, `entity`, `dto`, `mapper`.
- Chaque opération a son interface et son implémentation : `ProductReader` / `ProductReaderService`, `SaleValidator` / `SaleValidatorService`. Les opérations métier qui ne sont pas du CRUD ont leur propre package (`validator`, `receiver`, `decision`, `profile`, `checkin`...).
- Les controllers sont fins et dépendent des interfaces, pas des implémentations.
- Seuls des DTO (`...Request`, `...Response`) entrent et sortent ; un `Mapper` convertit entité et DTO. Aucune entité JPA n'est exposée.
- Les erreurs sont centralisées dans un `GlobalExceptionHandler` par service, au format commun `{status, type, message, timestamp}`.
- 100 lignes maximum par fichier, une responsabilité par fichier.

## Conséquences

- Chaque classe se teste seule avec Mockito ; les controllers se testent avec `@WebMvcTest`. Total actuel : 385 tests Java.
- Une entité sans modification possible n'a tout simplement pas d'Updater ni de Deleter (mouvements de stock, échanges CRM) : la règle métier se lit dans la structure.
- Beaucoup de petits fichiers ; il faut connaître la convention pour s'y retrouver.
- État actuel : tout le code Java de production respecte les 100 lignes. Quelques fichiers de test Java et plusieurs fichiers de l'ai-service Python (dont `orchestrator.py`) dépassent encore la limite.

## Alternatives écartées

- **Un service par entité** (`ProductService`) : moins de fichiers, mais des classes longues qui mêlent lecture et écriture.
- **CQRS complet** (modèles et bases séparés pour lecture et écriture) : trop lourd ; la séparation par classe suffit.
- **Exposer les entités JPA** : moins de code, mais couple l'API au schéma de la base et risque d'exposer des champs internes.
