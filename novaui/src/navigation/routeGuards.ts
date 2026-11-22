import { redirect } from '@tanstack/react-router'
import { ERROR_CONTENT } from '../constants/errorContent'
import { readSession } from '../services/sessionStore'
import { canAccessModule, sessionRoles, type ErpModule } from '../utils/roleAccess'
import { ROUTES } from './routes'

export const forbiddenRedirect = () =>
  redirect({
    to: ROUTES.error,
    replace: true,
    state: {
      error: {
        type: 'forbidden',
        message: ERROR_CONTENT.forbidden.message,
        from: ROUTES.home,
        status: 403,
        code: 'FORBIDDEN',
      },
    },
  })

export const requireModule = (module: ErpModule) => () => {
  const session = readSession()
  if (session && !canAccessModule(sessionRoles(session), module)) throw forbiddenRedirect()
}

export const requireAdmin = () => {
  const session = readSession()
  if (session && session.role !== 'ADMIN') throw forbiddenRedirect()
}
