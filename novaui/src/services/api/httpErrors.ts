import { ApiError } from '../../types/apiError'
import { toErrorType } from '../../utils/errorType'

export const DEFAULT_MESSAGE = 'Le serveur a renvoyé une erreur.'
export const NETWORK_MESSAGE = 'Le serveur est injoignable. Vérifiez votre connexion.'
export const TIMEOUT_MESSAGE = 'Le serveur met trop de temps à répondre. Réessayez dans un instant.'

interface ErrorBody {
  type?: string
  message?: string
}

export const toResponseError = async (response: Response): Promise<ApiError> => {
  const body = (await response.json().catch(() => null)) as ErrorBody | null
  const type = toErrorType(response.status, body?.type)
  return new ApiError(type, response.status, body?.message ?? DEFAULT_MESSAGE, body?.type)
}

const isTimeout = (error: unknown): boolean =>
  error instanceof DOMException && error.name === 'TimeoutError'

export const toTransportError = (error: unknown): ApiError =>
  isTimeout(error)
    ? new ApiError('network', 0, TIMEOUT_MESSAGE, 'TIMEOUT')
    : new ApiError('network', 0, NETWORK_MESSAGE)
