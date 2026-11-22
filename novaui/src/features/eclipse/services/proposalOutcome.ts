import { ADAPTATION_MODULE, isAdaptationKey } from '../constants/adaptations'
import type { AdaptationProposal } from '../types/adaptation'
import type { EclipseProposalResponse } from '../types/proposal'

export const toPreview = (response: EclipseProposalResponse): AdaptationProposal | null => {
  const key = response.adaptationKey
  if (response.outcome !== 'ADAPTATION' || !key || !isAdaptationKey(key)) return null
  return {
    adaptationKey: key,
    moduleKey: ADAPTATION_MODULE[key],
    request: response.request,
    title: response.title ?? '',
    summary: response.summary ?? '',
  }
}

export const answerOf = (response: EclipseProposalResponse): string | null =>
  response.message ?? (response.outcome === 'CREATE_MODULE' ? 'La création de module arrive bientôt.' : null)
