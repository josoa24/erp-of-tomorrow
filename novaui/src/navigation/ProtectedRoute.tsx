import { Navigate, Outlet } from 'react-router-dom'
import { readSession } from '../services/sessionStore'
import { ROUTES } from './routes'

export const ProtectedRoute = () =>
  readSession() ? <Outlet /> : <Navigate to={ROUTES.login} replace />
