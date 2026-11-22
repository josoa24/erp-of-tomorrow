import { ApiError } from '../../../types/apiError'
import { toApiError } from '../../../utils/toApiError'

export type EclipseUnavailability = 'unavailable' | 'rateLimited'

export const ECLIPSE_UNAVAILABLE_LABELS: Record<EclipseUnavailability, string> = {
  unavailable: 'Eclipse est indisponible pour le moment',
  rateLimited: 'Trop de demandes, réessayez dans une minute',
}

export const ECLIPSE_RETRY_DELAY_MS = 60_000

const GEMINI_RATE_LIMIT = 'GEMINI_RATE_LIMIT'
const GEMINI_PREFIX = 'GEMINI_'

export const unavailabilityOf = (error: unknown): EclipseUnavailability | null => {
  const { status } = toApiError(error)
  if (status === 429) return 'rateLimited'
  if (status === 503) return 'unavailable'
  return null
}

export const streamErrorToApiError = ({ code, message }: { code: string; message: string }): ApiError => {
  if (code === GEMINI_RATE_LIMIT) return new ApiError('server', 429, message, code)
  if (code.startsWith(GEMINI_PREFIX)) return new ApiError('network', 503, message, code)
  return new ApiError('server', 500, message, code)
}
