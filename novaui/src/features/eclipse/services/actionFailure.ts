import { toApiError } from '../../../utils/toApiError'
import type { ActionStatus } from '../types/agent'

export interface ActionFailure {
  status: Extract<ActionStatus, 'refused' | 'expired'>
  detail: string
  code: string
}

export const actionFailureOf = (error: unknown): ActionFailure | null => {
  const apiError = toApiError(error)
  const code = `${apiError.code ?? apiError.type.toUpperCase()} · ${apiError.status}`
  if (apiError.type === 'notFound') return { status: 'expired', detail: apiError.message, code }
  if (apiError.type === 'business' || apiError.type === 'validation') return { status: 'refused', detail: apiError.message, code }
  return null
}
