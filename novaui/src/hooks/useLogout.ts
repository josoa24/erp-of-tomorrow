import { useCallback } from 'react'
import { useNavigate } from 'react-router-dom'
import { ROUTES } from '../navigation/routes'
import { clearSession } from '../services/sessionStore'

export const useLogout = () => {
  const navigate = useNavigate()

  return useCallback(() => {
    clearSession()
    navigate(ROUTES.login, { replace: true })
  }, [navigate])
}
