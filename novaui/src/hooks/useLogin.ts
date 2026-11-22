import { useNavigate } from '@tanstack/react-router'
import { useCallback, useRef, useState } from 'react'
import { ROUTES } from '../navigation/routes'
import { login } from '../services/api/authApi'
import { forgetLoginEmail, readLoginEmail, rememberLoginEmail } from '../services/loginEmailStore'
import { saveSession } from '../services/sessionStore'
import type { LoginRequest } from '../types/auth'
import { useApiError } from './useApiError'

export const useLogin = () => {
  const navigate = useNavigate()
  const handleError = useApiError()
  const [submitting, setSubmitting] = useState(false)
  const [initialEmail] = useState(readLoginEmail)
  const pending = useRef(false)

  const submit = useCallback(
    async (credentials: LoginRequest) => {
      if (pending.current) return
      pending.current = true
      setSubmitting(true)
      try {
        saveSession(await login(credentials))
        forgetLoginEmail()
        await navigate({ to: ROUTES.home, replace: true })
      } catch (error) {
        rememberLoginEmail(credentials.email)
        handleError(error)
      } finally {
        pending.current = false
        setSubmitting(false)
      }
    },
    [navigate, handleError],
  )

  return { submit, submitting, initialEmail }
}
