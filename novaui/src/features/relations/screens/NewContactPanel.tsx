import { NewContactDialog } from '../components/NewContactDialog'
import { useNewContact } from '../hooks/useNewContact'
import type { AdaptationView } from '../types/adaptationView'

interface NewContactPanelProps {
  adaptation: AdaptationView
  onClose: () => void
}

export const NewContactPanel = ({ adaptation, onClose }: NewContactPanelProps) => {
  const { values, errors, update, submit, creating } = useNewContact(adaptation.visible, onClose)

  return (
    <NewContactDialog
      values={values}
      errors={errors}
      adaptation={adaptation}
      creating={creating}
      onChange={update}
      onSubmit={submit}
      onClose={onClose}
    />
  )
}
