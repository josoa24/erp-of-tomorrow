import type { ErrorType } from '../types/apiError'

const BY_BACKEND_TYPE: Record<string, ErrorType> = {
  NOT_FOUND: 'notFound',
  VALIDATION: 'validation',
  IMMUTABLE: 'business',
  BUSINESS: 'business',
  UNAUTHORIZED: 'unauthorized',
  FORBIDDEN: 'forbidden',
  SERVER: 'server',
}

export const toErrorType = (status: number, backendType?: string): ErrorType => {
  if (backendType && BY_BACKEND_TYPE[backendType]) return BY_BACKEND_TYPE[backendType]
  if (status === 401) return 'unauthorized'
  if (status === 403) return 'forbidden'
  if (status === 404) return 'notFound'
  if (status === 409) return 'business'
  if (status >= 400 && status < 500) return 'validation'
  return 'server'
}
