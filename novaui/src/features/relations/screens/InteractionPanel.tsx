import { InteractionDialog } from '../components/InteractionDialog'
import { useNewInteraction } from '../hooks/useNewInteraction'
import type { Contact } from '../types/contact'

interface InteractionPanelProps {
  contact: Contact
  onClose: () => void
}

export const InteractionPanel = ({ contact, onClose }: InteractionPanelProps) => {
  const { values, errors, today, update, submit, recording } = useNewInteraction(contact, onClose)

  return (
    <InteractionDialog
      contactName={contact.name}
      values={values}
      errors={errors}
      today={today}
      recording={recording}
      onChange={update}
      onSubmit={submit}
      onClose={onClose}
    />
  )
}
