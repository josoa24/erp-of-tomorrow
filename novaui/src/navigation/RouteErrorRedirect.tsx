import type { ErrorComponentProps } from '@tanstack/react-router'
import { useLocation } from '@tanstack/react-router'
import { useEffect } from 'react'
import { useApiError } from '../hooks/useApiError'
import { ApiError } from '../types/apiError'
import { ROUTES } from './routes'

export const UNEXPECTED_SCREEN_ERROR = 'Cet écran a rencontré une erreur inattendue. Réessayez dans un instant.'

const toDisplayedError = (error: unknown): ApiError =>
  error instanceof ApiError ? error : new ApiError('server', 0, UNEXPECTED_SCREEN_ERROR)

export const RouteErrorRedirect = ({ error }: ErrorComponentProps) => {
  const handleError = useApiError()
  const onErrorPage = useLocation({ select: (location) => location.pathname === ROUTES.error })

  useEffect(() => {
    if (!onErrorPage) handleError(toDisplayedError(error))
  }, [error, handleError, onErrorPage])

  return onErrorPage ? <p role="alert">{UNEXPECTED_SCREEN_ERROR}</p> : null
}
