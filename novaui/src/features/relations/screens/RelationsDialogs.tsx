import type { ContactDialog } from '../hooks/useContactDialogs'
import type { AdaptationView } from '../types/adaptationView'
import type { Contact } from '../types/contact'
import { InteractionPanel } from './InteractionPanel'
import { NewContactPanel } from './NewContactPanel'
import { TimelinePanel } from './TimelinePanel'

interface RelationsDialogsProps {
  dialog: ContactDialog | null
  adaptation: AdaptationView
  onRecord: (contact: Contact) => void
  onClose: () => void
}

export const RelationsDialogs = ({ dialog, adaptation, onRecord, onClose }: RelationsDialogsProps) => {
  if (dialog === null) return null
  if (dialog.type === 'create') return <NewContactPanel adaptation={adaptation} onClose={onClose} />
  if (dialog.type === 'interaction') return <InteractionPanel contact={dialog.contact} onClose={onClose} />
  return <TimelinePanel contact={dialog.contact} onRecord={() => onRecord(dialog.contact)} onClose={onClose} />
}
