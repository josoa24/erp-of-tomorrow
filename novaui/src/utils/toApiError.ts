import { ApiError } from '../types/apiError'

export const toApiError = (error: unknown): ApiError => {
  if (error instanceof ApiError) return error
  const message = error instanceof Error ? error.message : 'Une erreur inattendue est survenue.'
  return new ApiError('server', 0, message)
}
