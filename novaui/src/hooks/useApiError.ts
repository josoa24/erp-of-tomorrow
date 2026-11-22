import { useLocation, useNavigate } from '@tanstack/react-router'
import { useCallback } from 'react'
import { ROUTES } from '../navigation/routes'
import { clearSession } from '../services/sessionStore'
import { toApiError } from '../utils/toApiError'

export const useApiError = () => {
  const navigate = useNavigate()
  const pathname = useLocation({ select: (location) => location.pathname })

  return useCallback(
    (error: unknown) => {
      const apiError = toApiError(error)
      if (apiError.type === 'unauthorized') clearSession()
      void navigate({
        to: ROUTES.error,
        state: { error: { type: apiError.type, message: apiError.message, from: pathname } },
      })
    },
    [navigate, pathname],
  )
}
