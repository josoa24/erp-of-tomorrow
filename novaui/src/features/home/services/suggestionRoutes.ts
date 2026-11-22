import { ROUTES } from '../../../navigation/routes'
import type { ErpModule } from '../../../utils/roleAccess'

export const SUGGESTION_ROUTES: Record<string, typeof ROUTES.sales | typeof ROUTES.stock | typeof ROUTES.team | typeof ROUTES.relations> = {
  ventes: ROUTES.sales,
  stock: ROUTES.stock,
  relations: ROUTES.relations,
  equipe: ROUTES.team,
}

export const SUGGESTION_MODULES: Record<string, ErpModule> = {
  ventes: 'vente',
  stock: 'stock',
  relations: 'crm',
  equipe: 'rh',
}
