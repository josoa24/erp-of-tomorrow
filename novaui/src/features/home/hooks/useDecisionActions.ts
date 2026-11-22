import { useNavigate } from '@tanstack/react-router'
import { useState } from 'react'
import { isAdaptationKey } from '../../eclipse/constants/adaptations'
import { ROUTES } from '../../../navigation/routes'
import type { Decision } from '../services/homeDecisions'
import { SUGGESTION_ROUTES } from '../services/suggestionRoutes'
import type { EclipseSuggestion, Replenishment } from '../types/home'

export const useDecisionActions = (replenishment: Replenishment | undefined, suggestion: EclipseSuggestion | undefined) => {
  const navigate = useNavigate()
  const [confirming, setConfirming] = useState(false)

  const act = (decision: Decision) => {
    if (decision.kind === 'leaves') {
      void navigate({ to: ROUTES.team, search: { vue: 'demandes' } })
    } else if (decision.kind === 'suggestion' && suggestion) {
      void navigate({ to: SUGGESTION_ROUTES[suggestion.moduleKey] ?? ROUTES.home, search: isAdaptationKey(suggestion.adaptationKey) ? { eclipse: suggestion.adaptationKey } : {} })
    } else if (replenishment?.suggestedSupplierId && replenishment.recommendedQuantity) {
      setConfirming(true)
    } else {
      void navigate({ to: ROUTES.stock })
    }
  }

  return { act, confirming, closeConfirm: () => setConfirming(false) }
}
