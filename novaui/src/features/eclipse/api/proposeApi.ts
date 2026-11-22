import { httpPost } from '../../../services/api/httpClient'
import type { EclipseProposalResponse } from '../types/proposal'

export const PROPOSE_PATH = '/api/ai/eclipse/adaptations/propose'

export const proposeAdaptation = (moduleKey: string, request: string): Promise<EclipseProposalResponse> =>
  httpPost<EclipseProposalResponse>(PROPOSE_PATH, { moduleKey, request })
