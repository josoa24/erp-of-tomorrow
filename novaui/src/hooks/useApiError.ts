import { useLocation, useNavigate } from '@tanstack/react-router'
import { useCallback } from 'react'
import { ROUTES } from '../navigation/routes'
import { clearSession } from '../services/sessionStore'
import type { ApiError } from '../types/apiError'
import type { ErrorRouteState } from '../types/errorRoute'
import { toApiError } from '../utils/toApiError'

const toRouteState = (apiError: ApiError, from: string): ErrorRouteState => ({
  type: apiError.type,
  message: apiError.message,
  from,
  ...(apiError.status > 0 && { status: apiError.status }),
  ...(apiError.code && { code: apiError.code }),
})

export const useApiError = () => {
  const navigate = useNavigate()
  const pathname = useLocation({ select: (location) => location.pathname })

  return useCallback(
    (error: unknown) => {
      const apiError = toApiError(error)
      if (apiError.type === 'unauthorized') clearSession()
      void navigate({ to: ROUTES.error, state: { error: toRouteState(apiError, pathname) } })
    },
    [navigate, pathname],
  )
}
