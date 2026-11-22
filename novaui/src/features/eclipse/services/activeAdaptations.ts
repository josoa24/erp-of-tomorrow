import type { RequestOptions } from '../../../services/api/httpClient'
import { ApiError } from '../../../types/apiError'
import { fetchAdaptations } from '../api/adaptationsApi'
import type { AdaptationActivation } from '../types/adaptation'

const MODULE_SERVICE_MISSING_STATUSES = [404, 502, 503, 504]

export const NO_ACTIVE_ADAPTATIONS: readonly AdaptationActivation[] = []

export const isModuleServiceMissing = (error: unknown): boolean =>
  error instanceof ApiError && MODULE_SERVICE_MISSING_STATUSES.includes(error.status)

export const loadActiveAdaptations = async (
  options: RequestOptions = {},
): Promise<readonly AdaptationActivation[]> => {
  try {
    return await fetchAdaptations(options)
  } catch (error) {
    if (isModuleServiceMissing(error)) return NO_ACTIVE_ADAPTATIONS
    throw error
  }
}
