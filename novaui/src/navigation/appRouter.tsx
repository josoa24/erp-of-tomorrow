import { createBrowserRouter } from 'react-router-dom'
import { DashboardScreen } from '../screens/Dashboard/DashboardScreen'
import { ErrorScreen } from '../screens/Error/ErrorScreen'
import { LoginScreen } from '../screens/Login/LoginScreen'
import { ProtectedRoute } from './ProtectedRoute'
import { ROUTES } from './routes'

export const appRouter = createBrowserRouter([
  { path: ROUTES.login, element: <LoginScreen /> },
  { path: ROUTES.error, element: <ErrorScreen /> },
  {
    element: <ProtectedRoute />,
    children: [{ path: ROUTES.home, element: <DashboardScreen /> }],
  },
  { path: '*', element: <ErrorScreen /> },
])
