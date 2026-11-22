import type { ReactNode } from 'react'
import { EclipseAdaptationContext } from '../hooks/eclipse-adaptation-context'
import { useAdaptationPreviewState } from '../hooks/useAdaptationPreviewState'

export const EclipseAdaptationProvider = ({ children }: { children: ReactNode }) => (
  <EclipseAdaptationContext.Provider value={useAdaptationPreviewState()}>{children}</EclipseAdaptationContext.Provider>
)
