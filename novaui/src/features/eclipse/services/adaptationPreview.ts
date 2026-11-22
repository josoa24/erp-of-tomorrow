import { ADAPTATION_CATALOG, ADAPTATION_MODULE } from '../constants/adaptations'
import type { AdaptationKey, AdaptationModuleKey, AdaptationProposal } from '../types/adaptation'

export type StoredPreviews = Partial<Record<AdaptationModuleKey, AdaptationProposal>>

export const resolvePreview = (
  searchKey: AdaptationKey | undefined,
  stored: StoredPreviews,
): AdaptationProposal | null => {
  if (!searchKey) return null
  const proposal = stored[ADAPTATION_MODULE[searchKey]]
  return proposal?.adaptationKey === searchKey ? proposal : ADAPTATION_CATALOG[searchKey]
}

export const belongsToModule = (searchKey: AdaptationKey | undefined, moduleKey: AdaptationModuleKey): boolean =>
  searchKey !== undefined && ADAPTATION_MODULE[searchKey] === moduleKey

export const withPreview = (stored: StoredPreviews, proposal: AdaptationProposal): StoredPreviews => ({
  ...stored,
  [proposal.moduleKey]: proposal,
})

export const withoutPreview = (stored: StoredPreviews, moduleKey: AdaptationModuleKey): StoredPreviews => {
  const remaining = { ...stored }
  delete remaining[moduleKey]
  return remaining
}
