import { useNavigate } from '@tanstack/react-router'
import { useCallback } from 'react'
import { queryClient } from '../lib/queryClient'
import { ROUTES } from '../navigation/routes'
import { clearSession } from '../services/sessionStore'

export const useLogout = () => {
  const navigate = useNavigate()

  return useCallback(() => {
    clearSession()
    queryClient.clear()
    void navigate({ to: ROUTES.login, replace: true })
  }, [navigate])
}
