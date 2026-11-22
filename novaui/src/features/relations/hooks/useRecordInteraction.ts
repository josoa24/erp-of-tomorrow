import { useMutation, useQueryClient } from '@tanstack/react-query'
import { useToast } from '../../../components/ui/toast-context'
import { useApiError } from '../../../hooks/useApiError'
import { recordInteraction } from '../api/partiesApi'
import { RELATIONS_QUERY_KEY } from '../constants/queryKeys'
import type { PartyRef } from '../types/contact'
import type { InteractionRequest } from '../types/interaction'

interface RecordCommand {
  party: PartyRef
  request: InteractionRequest
}

export const useRecordInteraction = (onRecorded: () => void) => {
  const queryClient = useQueryClient()
  const handleError = useApiError()
  const { showToast } = useToast()

  const mutation = useMutation({
    mutationFn: ({ party, request }: RecordCommand) => recordInteraction(party, request),
    onSuccess: () => {
      showToast({ message: 'Échange enregistré' })
      onRecorded()
      return queryClient.invalidateQueries({ queryKey: RELATIONS_QUERY_KEY })
    },
    onError: handleError,
  })

  return { record: mutation.mutate, recording: mutation.isPending }
}
