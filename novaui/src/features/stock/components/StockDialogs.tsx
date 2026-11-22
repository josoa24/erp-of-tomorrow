import type { StockDialog } from '../hooks/useStockDialogs'
import type { StockAdaptationView } from '../services/stockAdaptation'
import { ProductLotsDialog } from './ProductLotsDialog'
import { StockEntryDialog } from './StockEntryDialog'
import { StockExitDialog } from './StockExitDialog'

interface StockDialogsProps {
  dialog: StockDialog
  adaptation: StockAdaptationView
  onClose: () => void
}

export const StockDialogs = ({ dialog, adaptation, onClose }: StockDialogsProps) => {
  if (dialog?.kind === 'entry') return <StockEntryDialog item={dialog.item} adaptation={adaptation} onClose={onClose} />
  if (dialog?.kind === 'exit') return <StockExitDialog item={dialog.item} withLots={adaptation.enabled} onClose={onClose} />
  if (dialog?.kind === 'lots' && adaptation.enabled) return <ProductLotsDialog item={dialog.item} onClose={onClose} />
  return null
}
