import { TimelineDialog } from '../components/TimelineDialog'
import { useTimeline } from '../hooks/useTimeline'
import type { Contact } from '../types/contact'

interface TimelinePanelProps {
  contact: Contact
  onRecord: () => void
  onClose: () => void
}

export const TimelinePanel = ({ contact, onRecord, onClose }: TimelinePanelProps) => {
  const { entries, loading } = useTimeline(contact)

  return (
    <TimelineDialog contactName={contact.name} entries={entries} loading={loading} onRecord={onRecord} onClose={onClose} />
  )
}
