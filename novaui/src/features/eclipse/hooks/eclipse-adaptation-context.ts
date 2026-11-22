import { createContext, useContext } from 'react'
import type { AdaptationModuleKey, AdaptationProposal } from '../types/adaptation'

export interface EclipseAdaptationContextValue {
  preview: AdaptationProposal | null
  startPreview: (proposal: AdaptationProposal) => void
  clearPreview: (moduleKey: AdaptationModuleKey) => void
}

const noop = () => undefined

export const EclipseAdaptationContext = createContext<EclipseAdaptationContextValue>({
  preview: null,
  startPreview: noop,
  clearPreview: noop,
})

export const useEclipseAdaptation = (): EclipseAdaptationContextValue => useContext(EclipseAdaptationContext)
