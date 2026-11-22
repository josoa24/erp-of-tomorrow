import { useMutation, useQueryClient } from '@tanstack/react-query'
import { useToast } from '../../../components/ui/toast-context'
import { useApiError } from '../../../hooks/useApiError'
import { createContact } from '../api/contactsApi'
import { RELATIONS_QUERY_KEY } from '../constants/queryKeys'

export const useCreateContact = (onCreated: () => void) => {
  const queryClient = useQueryClient()
  const handleError = useApiError()
  const { showToast } = useToast()

  const mutation = useMutation({
    mutationFn: createContact,
    onSuccess: (contact) => {
      showToast({ message: `Contact « ${contact.name} » ajouté` })
      onCreated()
      return queryClient.invalidateQueries({ queryKey: RELATIONS_QUERY_KEY })
    },
    onError: handleError,
  })

  return { create: mutation.mutate, creating: mutation.isPending }
}
