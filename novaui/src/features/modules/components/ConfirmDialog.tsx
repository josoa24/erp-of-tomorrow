import { Button } from '../../../components/ui/button'
import { Dialog } from '../../../components/ui/dialog'

interface ConfirmDialogProps {
  title: string
  description: string
  confirmLabel: string
  danger?: boolean
  loading: boolean
  onConfirm: () => void
  onClose: () => void
}

export const ConfirmDialog = ({ title, description, confirmLabel, danger = false, loading, onConfirm, onClose }: ConfirmDialogProps) => (
  <Dialog
    open
    onClose={onClose}
    title={title}
    description={description}
    footer={
      <>
        <Button variant="secondary" onClick={onClose} disabled={loading}>Annuler</Button>
        <Button variant={danger ? 'danger' : 'primary'} loading={loading} onClick={onConfirm}>
          {confirmLabel}
        </Button>
      </>
    }
  />
)
