import { useCallback } from 'react'
import { useCurrentUser } from '../../../hooks/useCurrentUser'
import type { AdaptationModuleKey, AdaptationProposal } from '../types/adaptation'
import { useEclipseAdaptation } from './eclipse-adaptation-context'
import { useActiveAdaptations } from './useActiveAdaptations'
import { useApplyAdaptation } from './useApplyAdaptation'

export interface ModuleAdaptation {
  active: boolean
  preview: AdaptationProposal | null
  isApplying: boolean
  apply: () => void
  cancel: () => void
  canApply: boolean
}

export const useModuleAdaptation = (moduleKey: AdaptationModuleKey): ModuleAdaptation => {
  const activations = useActiveAdaptations()
  const { preview: currentPreview, clearPreview } = useEclipseAdaptation()
  const canApply = useCurrentUser()?.role === 'ADMIN'
  const active = activations.some((activation) => activation.moduleKey === moduleKey)
  const preview = !active && currentPreview?.moduleKey === moduleKey ? currentPreview : null
  const cancel = useCallback(() => clearPreview(moduleKey), [clearPreview, moduleKey])
  const { apply, isApplying } = useApplyAdaptation(preview, cancel)

  return { active, preview, isApplying, apply, cancel, canApply }
}
