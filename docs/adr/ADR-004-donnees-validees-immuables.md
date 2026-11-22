# ADR-004 : données validées immuables, correction par nouvelle écriture

Statut : acceptée.

## Contexte

Dans un ERP, une vente validée a fait sortir du stock, un congé validé a débité un solde, un bon de commande réceptionné a fait entrer de la marchandise. Modifier ces données après coup ferait perdre la trace de ce qui s'est passé et rendrait les chiffres incohérents. C'est aussi un garde-fou quand une IA peut proposer des écritures.

## Décision

- Les documents validables ont un statut piloté par le backend :
  - vente : `DRAFT`, `VALIDATED` (`DocumentStatus`) ;
  - bon de commande : `DRAFT`, `VALIDATED`, `RECEIVED` (`OrderStatus`) ;
  - congé : `DRAFT`, `VALIDATED`, `REJECTED` (`LeaveStatus`).
- Seul un brouillon (`DRAFT`) se modifie ou se supprime (une vente brouillon, par exemple ; les bons de commande et les congés n'ont pas d'opération de modification). Sur un document déjà validé, l'Updater, le Deleter ou le Validator lève une `ImmutableResourceException`, renvoyée en **409** avec le type `IMMUTABLE` (vente-service, stock-service, rh-service).
- Les données purement historiques n'ont ni Updater ni Deleter : mouvements de stock (`StockMovement`), échanges CRM. Elles sont créées une fois.
- Une erreur se corrige par une nouvelle écriture : mouvement inverse, nouvelle vente. La compensation d'une validation de vente échouée suit cette règle : elle crée des mouvements d'entrée « Annulation vente #id », elle ne supprime pas les sorties.
- Le front doit refléter le statut (champs en lecture seule), mais la garantie est côté serveur.

## Conséquences

- L'historique est fiable : le stock d'un produit s'explique par la suite de ses mouvements.
- L'ERP refuse aussi une écriture proposée par l'IA sur une donnée validée, quoi que dise le modèle.
- Les erreurs de saisie demandent une écriture de plus, ce qui est plus lourd pour l'utilisateur.
- Il n'existe pas encore d'avoir ni d'annulation formelle de vente validée : la correction passe par un mouvement de stock.
- Il n'y a pas encore de journal d'audit « qui a fait quoi » (tâche SYS-4).

## Alternatives écartées

- **Modification libre avec historique des versions** : garde une trace, mais les effets déjà produits (stock débité, solde de congés) ne sont pas recalculés.
- **Suppression logique** (`deleted = true`) : masque l'erreur sans l'expliquer, et les totaux deviennent ambigus.
- **Contrôle seulement dans le front** : contournable par un appel direct à l'API.
