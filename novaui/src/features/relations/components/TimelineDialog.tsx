import { Button } from '../../../components/ui/button'
import { Dialog } from '../../../components/ui/dialog'
import type { TimelineEntry } from '../types/interaction'
import { TimelineList } from './TimelineList'

interface TimelineDialogProps {
  contactName: string
  entries: TimelineEntry[]
  loading: boolean
  onRecord: () => void
  onClose: () => void
}

export const TimelineDialog = ({ contactName, entries, loading, onRecord, onClose }: TimelineDialogProps) => (
  <Dialog
    open
    onClose={onClose}
    title="Chronologie"
    description={`Échanges, ventes et commandes avec ${contactName}, du plus récent au plus ancien.`}
    footer={
      <>
        <Button variant="secondary" onClick={onClose}>
          Fermer
        </Button>
        <Button onClick={onRecord}>Enregistrer un échange</Button>
      </>
    }
  >
    <TimelineList entries={entries} loading={loading} />
  </Dialog>
)
