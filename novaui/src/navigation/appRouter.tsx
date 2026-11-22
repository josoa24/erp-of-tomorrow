import type { RouterHistory } from '@tanstack/react-router'
import { Outlet, createRootRoute, createRoute, createRouter, redirect } from '@tanstack/react-router'
import { DashboardScreen } from '../screens/Dashboard/DashboardScreen'
import { ErrorScreen } from '../screens/Error/ErrorScreen'
import { LoginScreen } from '../screens/Login/LoginScreen'
import { readSession } from '../services/sessionStore'
import { ROUTES } from './routes'

const rootRoute = createRootRoute({
  component: Outlet,
  notFoundComponent: ErrorScreen,
})

const loginRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: ROUTES.login,
  beforeLoad: () => {
    if (readSession()) throw redirect({ to: ROUTES.home, replace: true })
  },
  component: LoginScreen,
})

const errorRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: ROUTES.error,
  component: ErrorScreen,
})

const protectedRoute = createRoute({
  getParentRoute: () => rootRoute,
  id: 'protected',
  beforeLoad: () => {
    if (!readSession()) throw redirect({ to: ROUTES.login, replace: true })
  },
  component: Outlet,
})

const homeRoute = createRoute({
  getParentRoute: () => protectedRoute,
  path: ROUTES.home,
  component: DashboardScreen,
})

const routeTree = rootRoute.addChildren([loginRoute, errorRoute, protectedRoute.addChildren([homeRoute])])

export const createAppRouter = (history?: RouterHistory) => createRouter({ routeTree, history })

export const appRouter = createAppRouter()

declare module '@tanstack/react-router' {
  interface Register {
    router: typeof appRouter
  }
}
