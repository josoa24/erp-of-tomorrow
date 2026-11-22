import { Outlet, createRootRoute, createRoute, redirect } from '@tanstack/react-router'
import { ErrorScreen } from '../screens/Error/ErrorScreen'
import { LoginScreen } from '../screens/Login/LoginScreen'
import { readSession } from '../services/sessionStore'
import { ROUTES } from './routes'
import { validateShellSearch } from './shellSearch'

export const rootRoute = createRootRoute({
  component: Outlet,
  notFoundComponent: ErrorScreen,
})

export const loginRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: ROUTES.login,
  beforeLoad: () => {
    if (readSession()) throw redirect({ to: ROUTES.home, replace: true })
  },
  component: LoginScreen,
})

export const errorRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: ROUTES.error,
  component: ErrorScreen,
})

export const protectedRoute = createRoute({
  getParentRoute: () => rootRoute,
  id: 'protected',
  validateSearch: validateShellSearch,
  beforeLoad: () => {
    if (!readSession()) throw redirect({ to: ROUTES.login, replace: true })
  },
  component: Outlet,
})
