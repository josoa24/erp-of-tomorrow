import { createRoute } from '@tanstack/react-router'
import { ECLIPSE_PLACEHOLDERS } from '../constants/eclipsePlaceholders'
import { CustomModuleScreen } from '../features/modules/screens/CustomModuleScreen'
import { ProfileScreen } from '../features/profile/screens/ProfileScreen'
import { RelationsScreen } from '../features/relations/screens/RelationsScreen'
import { SalesScreen } from '../features/sales/screens/SalesScreen'
import { StockScreen } from '../features/stock/screens/StockScreen'
import { TeamScreen } from '../features/team/screens/TeamScreen'
import { DashboardScreen } from '../screens/Dashboard/DashboardScreen'
import { protectedRoute } from './baseRoutes'
import { requireModule } from './routeGuards'
import { ROUTES } from './routes'

const parent = () => protectedRoute

export const homeRoute = createRoute({
  getParentRoute: parent,
  path: ROUTES.home,
  staticData: { hideEclipseDock: true },
  component: DashboardScreen,
})

export const salesRoute = createRoute({
  getParentRoute: parent,
  path: ROUTES.sales,
  beforeLoad: requireModule('vente'),
  staticData: { eclipsePlaceholder: ECLIPSE_PLACEHOLDERS.sales },
  component: SalesScreen,
})

export const stockRoute = createRoute({
  getParentRoute: parent,
  path: ROUTES.stock,
  beforeLoad: requireModule('stock'),
  staticData: { eclipsePlaceholder: ECLIPSE_PLACEHOLDERS.stock },
  component: StockScreen,
})

export const teamRoute = createRoute({
  getParentRoute: parent,
  path: ROUTES.team,
  beforeLoad: requireModule('rh'),
  staticData: { eclipsePlaceholder: ECLIPSE_PLACEHOLDERS.team },
  component: TeamScreen,
})

export const relationsRoute = createRoute({
  getParentRoute: parent,
  path: ROUTES.relations,
  beforeLoad: requireModule('crm'),
  staticData: { eclipsePlaceholder: ECLIPSE_PLACEHOLDERS.relations },
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
  teamRoute,
  relationsRoute,
  profileRoute,
  customModuleRoute,
] as const
