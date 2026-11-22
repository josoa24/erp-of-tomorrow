import { Button } from '../../../components/ui/button'
import { Dialog } from '../../../components/ui/dialog'
import type { Decision } from '../types/decision'
import type { DecisionItem } from '../types/decisionItem'
import { DecisionRow } from './DecisionRow'

interface DecisionDialogProps {
  title: string
  description: string
  emptyText: string
  items: DecisionItem[] | undefined
  deciding: boolean
  onDecide: (id: number, decision: Decision) => void
  onClose: () => void
}

export const DecisionDialog = ({ title, description, emptyText, items, deciding, onDecide, onClose }: DecisionDialogProps) => (
  <Dialog
    open
    onClose={onClose}
    title={title}
    description={description}
    footer={<Button variant="secondary" onClick={onClose}>Fermer</Button>}
  >
    {items?.length === 0 && <p className="text-body text-ink-secondary">{emptyText}</p>}
    <ul className="flex flex-col gap-3">
      {items?.map((item) => (
        <DecisionRow key={item.id} item={item} deciding={deciding} onDecide={onDecide} />
      ))}
    </ul>
  </Dialog>
)
