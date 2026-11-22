import type { RouterHistory } from '@tanstack/react-router'
import { createRouter } from '@tanstack/react-router'
import { errorRoute, loginRoute, protectedRoute, rootRoute } from './baseRoutes'
import { moduleRoutes } from './moduleRoutes'
import { RouteErrorRedirect } from './RouteErrorRedirect'

const routeTree = rootRoute.addChildren([loginRoute, errorRoute, protectedRoute.addChildren(moduleRoutes)])

export const createAppRouter = (history?: RouterHistory) =>
  createRouter({ routeTree, history, defaultErrorComponent: RouteErrorRedirect })

export const appRouter = createAppRouter()

declare module '@tanstack/react-router' {
  interface Register {
    router: typeof appRouter
  }
}
