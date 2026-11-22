import { createRoute } from '@tanstack/react-router'
import { UsersScreen } from '../features/users/screens/UsersScreen'
import { validateUsersSearch } from '../features/users/services/usersSearch'
import { protectedRoute } from './baseRoutes'
import { requireAdmin } from './routeGuards'
import { ROUTES } from './routes'

export const usersRoute = createRoute({
  getParentRoute: () => protectedRoute,
  path: ROUTES.users,
  beforeLoad: requireAdmin,
  validateSearch: validateUsersSearch,
  staticData: { hideEclipseDock: true },
  component: UsersScreen,
})
