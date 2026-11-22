import { useNavigate } from '@tanstack/react-router'
import { useCallback } from 'react'
import { ROUTES } from '../navigation/routes'
import { clearSession } from '../services/sessionStore'

export const useLogout = () => {
  const navigate = useNavigate()

  return useCallback(() => {
    clearSession()
    void navigate({ to: ROUTES.login, replace: true })
  }, [navigate])
}
