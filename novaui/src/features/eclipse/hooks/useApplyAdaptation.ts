import { useMutation, useQueryClient } from '@tanstack/react-query'
import { useCallback } from 'react'
import { useToast } from '../../../components/ui/toast-context'
import { useApiError } from '../../../hooks/useApiError'
import { activateAdaptation } from '../api/adaptationsApi'
import type { AdaptationProposal } from '../types/adaptation'
import { ADAPTATIONS_QUERY_KEY } from './useActiveAdaptations'

export const ADAPTATION_APPLIED_MESSAGE = 'Adaptation appliquée pour l’équipe'

export const useApplyAdaptation = (preview: AdaptationProposal | null, onApplied: () => void) => {
  const queryClient = useQueryClient()
  const handleError = useApiError()
  const { showToast } = useToast()
  const { mutate, isPending } = useMutation({
    mutationFn: ({ moduleKey, adaptationKey }: AdaptationProposal) => activateAdaptation({ moduleKey, adaptationKey }),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ADAPTATIONS_QUERY_KEY })
      onApplied()
      showToast({ message: ADAPTATION_APPLIED_MESSAGE })
    },
    onError: handleError,
  })

  const apply = useCallback(() => {
    if (preview) mutate(preview)
  }, [preview, mutate])

  return { apply, isApplying: isPending }
}
