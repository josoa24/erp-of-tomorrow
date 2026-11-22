import { useMutation } from '@tanstack/react-query'
import { useCallback, useState } from 'react'
import { useApiError } from '../../../hooks/useApiError'
import { proposeModule, refineModule } from '../api/moduleProposeApi'
import { initialSelection, toDefinitionRequest, type ProposalSelection } from '../services/proposalSelection'
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

  const refinement = useMutation({
    mutationFn: (instruction: string) => {
      if (!proposal || !selection) throw new Error('Aucune proposition à ajuster')
      return refineModule(toDefinitionRequest(proposal, selection), instruction)
    },
    onSuccess: (result) => {
      setProposal(result)
      setSelection(initialSelection({ ...result, uncheckedColumns: [] }))
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
    refining: refinement.isPending,
    refine: (instruction: string) => refinement.mutate(instruction),
    setName,
    toggleColumn,
    toggleIndicator,
    backToDescription,
  }
}
