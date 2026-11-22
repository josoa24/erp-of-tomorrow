import { useMutation } from '@tanstack/react-query'
import { useCallback, useState } from 'react'
import { useApiError } from '../../../hooks/useApiError'
import { proposeModule } from '../api/moduleProposeApi'
import { initialSelection, type ProposalSelection } from '../services/proposalSelection'
import type { ModuleProposal } from '../types/moduleRecord'

export const useModuleProposal = (initialDescription: string) => {
  const handleError = useApiError()
  const [description, setDescription] = useState(initialDescription)
  const [proposal, setProposal] = useState<ModuleProposal | null>(null)
  const [selection, setSelection] = useState<ProposalSelection | null>(null)

  const mutation = useMutation({
    mutationFn: () => proposeModule(description.trim()),
    onSuccess: (result) => {
      setProposal(result)
      setSelection(initialSelection(result))
    },
    onError: handleError,
  })

  const setName = useCallback((name: string) => setSelection((previous) => previous && { ...previous, name }), [])
  const toggleColumn = useCallback(
    (key: string, checked: boolean) =>
      setSelection((previous) => previous && { ...previous, columns: { ...previous.columns, [key]: checked } }),
    [],
  )
  const toggleIndicator = useCallback(
    (key: string, checked: boolean) =>
      setSelection((previous) => previous && { ...previous, indicators: { ...previous.indicators, [key]: checked } }),
    [],
  )
  const backToDescription = useCallback(() => setProposal(null), [])

  return {
    description,
    setDescription,
    proposal,
    selection,
    preparing: mutation.isPending,
    prepare: () => mutation.mutate(),
    setName,
    toggleColumn,
    toggleIndicator,
    backToDescription,
  }
}
