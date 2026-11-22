import { useEffect } from 'react'
import { useApiError } from '../../../hooks/useApiError'

export const useErrorRedirect = (error: unknown) => {
  const handleError = useApiError()

  useEffect(() => {
    if (error) handleError(error)
  }, [error, handleError])
}
