import { useCallback, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { ROUTES } from '../navigation/routes'
import { login } from '../services/api/authApi'
import { saveSession } from '../services/sessionStore'
import type { LoginRequest } from '../types/auth'
import { useApiError } from './useApiError'

export const useLogin = () => {
  const navigate = useNavigate()
  const handleError = useApiError()
  const [submitting, setSubmitting] = useState(false)

  const submit = useCallback(
    async (credentials: LoginRequest, remember: boolean) => {
      setSubmitting(true)
      try {
        const session = await login(credentials)
        saveSession(session, remember)
        navigate(ROUTES.home, { replace: true })
      } catch (error) {
        setSubmitting(false)
        handleError(error)
      }
    },
    [navigate, handleError],
  )

  return { submit, submitting }
}
