import type { PartyRef } from '../types/contact'
import { useInteractionForm } from './useInteractionForm'
import { useRecordInteraction } from './useRecordInteraction'

export const useNewInteraction = (party: PartyRef, onDone: () => void) => {
  const form = useInteractionForm()
  const { record, recording } = useRecordInteraction(onDone)

  const submit = () => {
    const request = form.prepare()
    if (request) record({ party: { kind: party.kind, partyId: party.partyId }, request })
  }

  return { ...form, submit, recording }
}
