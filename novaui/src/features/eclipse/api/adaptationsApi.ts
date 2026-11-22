import { httpGet, httpPost, type RequestOptions } from '../../../services/api/httpClient'
import type { AdaptationActivation, AdaptationActivationRequest } from '../types/adaptation'

export const ADAPTATIONS_PATH = '/api/modules/adaptations'

export const fetchAdaptations = (options: RequestOptions = {}): Promise<AdaptationActivation[]> =>
  httpGet<AdaptationActivation[]>(ADAPTATIONS_PATH, options)

export const activateAdaptation = (
  activation: AdaptationActivationRequest,
  options: RequestOptions = {},
): Promise<AdaptationActivation> => httpPost<AdaptationActivation>(ADAPTATIONS_PATH, activation, options)
