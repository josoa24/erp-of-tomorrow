import { useNavigate } from '@tanstack/react-router'
import { useCallback, useState } from 'react'
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
        await navigate({ to: ROUTES.home, replace: true })
      } catch (error) {
        setSubmitting(false)
        handleError(error)
      }
    },
    [navigate, handleError],
  )

  return { submit, submitting }
}
