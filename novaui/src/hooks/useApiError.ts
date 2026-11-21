import { useCallback } from 'react'
import { useLocation, useNavigate } from 'react-router-dom'
import { ROUTES } from '../navigation/routes'
import { clearSession } from '../services/sessionStore'
import type { ErrorRouteState } from '../types/errorRoute'
import { toApiError } from '../utils/toApiError'

export const useApiError = () => {
  const navigate = useNavigate()
  const { pathname } = useLocation()

  return useCallback(
    (error: unknown) => {
      const apiError = toApiError(error)
      if (apiError.type === 'unauthorized') clearSession()
      const state: ErrorRouteState = { type: apiError.type, message: apiError.message, from: pathname }
      navigate(ROUTES.error, { state })
    },
    [navigate, pathname],
  )
}
