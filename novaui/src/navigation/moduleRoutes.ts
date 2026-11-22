import { createRoute } from '@tanstack/react-router'
import { ECLIPSE_PLACEHOLDERS } from '../constants/eclipsePlaceholders'
import { CustomModuleScreen } from '../features/modules/screens/CustomModuleScreen'
import { ProfileScreen } from '../features/profile/screens/ProfileScreen'
import { RelationsScreen } from '../features/relations/screens/RelationsScreen'
import { validateRelationsSearch } from '../features/relations/services/relationsSearch'
import { SalesScreen } from '../features/sales/screens/SalesScreen'
import { validateSalesSearch } from '../features/sales/services/salesSearch'
import { PurchaseOrdersScreen } from '../features/stock/screens/PurchaseOrdersScreen'
import { StockScreen } from '../features/stock/screens/StockScreen'
import { validateStockSearch } from '../features/stock/services/stockSearch'
import { TeamScreen } from '../features/team/screens/TeamScreen'
import { validateTeamSearch } from '../features/team/services/teamSearch'
import { HomeScreen } from '../features/home/screens/HomeScreen'
import { usersRoute } from './adminRoutes'
import { protectedRoute } from './baseRoutes'
import { requireModule } from './routeGuards'
import { ROUTES } from './routes'

const parent = () => protectedRoute

export const homeRoute = createRoute({
  getParentRoute: parent,
  path: ROUTES.home,
  staticData: { hideEclipseDock: true },
  component: HomeScreen,
})

export const salesRoute = createRoute({
  getParentRoute: parent,
  path: ROUTES.sales,
  beforeLoad: requireModule('vente'),
  validateSearch: validateSalesSearch,
  staticData: { eclipsePlaceholder: ECLIPSE_PLACEHOLDERS.sales, adaptationModule: 'ventes' },
  component: SalesScreen,
})

export const stockRoute = createRoute({
  getParentRoute: parent,
  path: ROUTES.stock,
  validateSearch: validateStockSearch,
  beforeLoad: requireModule('stock'),
  staticData: { eclipsePlaceholder: ECLIPSE_PLACEHOLDERS.stock, adaptationModule: 'stock' },
  component: StockScreen,
})

export const stockOrdersRoute = createRoute({
  getParentRoute: parent,
  path: ROUTES.stockOrders,
  beforeLoad: requireModule('stock'),
  staticData: { eclipsePlaceholder: ECLIPSE_PLACEHOLDERS.stock },
  component: PurchaseOrdersScreen,
})

export const teamRoute = createRoute({
  getParentRoute: parent,
  path: ROUTES.team,
  beforeLoad: requireModule('rh'),
  validateSearch: validateTeamSearch,
  staticData: { eclipsePlaceholder: ECLIPSE_PLACEHOLDERS.team, adaptationModule: 'equipe' },
  component: TeamScreen,
})

export const relationsRoute = createRoute({
  getParentRoute: parent,
  path: ROUTES.relations,
  beforeLoad: requireModule('crm'),
  validateSearch: validateRelationsSearch,
  staticData: { eclipsePlaceholder: ECLIPSE_PLACEHOLDERS.relations, adaptationModule: 'relations' },
  component: RelationsScreen,
})

export const profileRoute = createRoute({
  getParentRoute: parent,
  path: ROUTES.profile,
  staticData: { hideEclipseDock: true },
  component: ProfileScreen,
})

export const customModuleRoute = createRoute({
  getParentRoute: parent,
  path: ROUTES.customModule,
  component: CustomModuleScreen,
})

export const moduleRoutes = [
  homeRoute,
  salesRoute,
  stockRoute,
  stockOrdersRoute,
  teamRoute,
  relationsRoute,
  profileRoute,
  usersRoute,
  customModuleRoute,
] as const
