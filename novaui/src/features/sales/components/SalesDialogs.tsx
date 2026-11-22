import type { SalesDialog } from '../hooks/useSalesDialogs'
import { CancelDialog } from './CancelDialog'
import { CollectDialog } from './CollectDialog'
import { SaleDetailDialog } from './SaleDetailDialog'
import { SaleFormDialog } from './SaleFormDialog'

interface SalesDialogsProps {
  dialog: SalesDialog
  adapted: boolean
  highlighted: boolean
  onClose: () => void
}

export const SalesDialogs = ({ dialog, adapted, highlighted, onClose }: SalesDialogsProps) => {
  if (dialog?.kind === 'form') {
    return <SaleFormDialog key={dialog.sale?.id ?? 'new'} sale={dialog.sale} adapted={adapted} highlighted={highlighted} onClose={onClose} />
  }
  if (dialog?.kind === 'collect') return <CollectDialog sale={dialog.sale} onClose={onClose} />
  if (dialog?.kind === 'cancel') return <CancelDialog sale={dialog.sale} onClose={onClose} />
  if (dialog?.kind === 'detail') return <SaleDetailDialog sale={dialog.sale} onClose={onClose} />
  return null
}
