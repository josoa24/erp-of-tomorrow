import type { DataTableColumn } from '../../../components/module/types'
import { formatLastExchange } from '../services/lastExchange'
import type { AdaptationView } from '../types/adaptationView'
import type { Contact } from '../types/contact'
import { ContactCell } from './ContactCell'
import { ContactStatusPill } from './ContactStatusPill'
import { CreditLimitCell } from './CreditLimitCell'
import { EmptyValue } from './EmptyValue'
import { NifStatCell } from './NifStatCell'

const textOrEmpty = (value: string | null) => value || <EmptyValue />

const adaptedColumns = (isNew: boolean): DataTableColumn<Contact>[] => [
  { key: 'nifStat', header: 'NIF / STAT', width: 180, isNew, render: (contact) => <NifStatCell nif={contact.nif} stat={contact.stat} /> },
  { key: 'creditLimit', header: 'Plafond crédit', width: 210, isNew, render: (contact) => <CreditLimitCell {...contact} /> },
]

export const contactColumns = ({ visible, highlighted }: AdaptationView): DataTableColumn<Contact>[] => [
  { key: 'contact', header: 'Contact', width: visible ? 280 : 400, render: (contact) => <ContactCell contact={contact} /> },
  { key: 'city', header: 'Ville', width: visible ? 150 : 240, render: (contact) => textOrEmpty(contact.city) },
  { key: 'phone', header: 'Téléphone', width: visible ? 160 : 240, render: (contact) => textOrEmpty(contact.phone) },
  {
    key: 'lastExchange',
    header: 'Dernier échange',
    width: visible ? 150 : 240,
    render: (contact) => formatLastExchange(contact.lastInteractionAt),
  },
  ...(visible ? adaptedColumns(highlighted) : []),
  { key: 'status', header: 'Statut', width: 160, render: (contact) => <ContactStatusPill status={contact.status} /> },
]
