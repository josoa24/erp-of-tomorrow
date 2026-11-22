import { useEffect } from 'react'
import { useApiError } from '../../../hooks/useApiError'

export const useRedirectOnError = (error: unknown) => {
  const handleError = useApiError()

  useEffect(() => {
    if (error) handleError(error)
  }, [error, handleError])
}
