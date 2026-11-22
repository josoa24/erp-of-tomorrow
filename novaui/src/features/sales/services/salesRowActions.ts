import { Ban, Eye, HandCoins, Pencil, Send, Trash2 } from 'lucide-react'
import type { RowAction } from '../../../components/module/types'
import type { Sale } from '../types/sale'
import { saleActionKeys, type SaleActionKey } from './saleActions'

export type SaleRowHandlers = Record<SaleActionKey, (sale: Sale) => void>

const ACTION_VIEWS: Readonly<Record<SaleActionKey, Pick<RowAction, 'label' | 'icon' | 'tone'>>> = {
  edit: { label: 'Modifier', icon: Pencil },
  validate: { label: 'Valider la vente', icon: Send },
  delete: { label: 'Supprimer le brouillon', icon: Trash2, tone: 'danger' },
  collect: { label: 'Encaisser', icon: HandCoins },
  cancel: { label: 'Annuler la vente', icon: Ban, tone: 'danger' },
  detail: { label: 'Voir le détail', icon: Eye },
}

export const buildSaleRowActions = (sale: Sale, handlers: SaleRowHandlers): RowAction[] =>
  saleActionKeys(sale).map((key) => ({ key, ...ACTION_VIEWS[key], onSelect: () => handlers[key](sale) }))
