import type { RelationsFilter } from '../services/relationsSearch'
import type { ContactCounts } from '../types/contact'

export interface FilterOption {
  value?: RelationsFilter
  label: string
  countKey: keyof ContactCounts
  adaptation?: boolean
}

export const FILTER_OPTIONS: readonly FilterOption[] = [
  { label: 'Tous', countKey: 'all' },
  { value: 'clients', label: 'Clients', countKey: 'customers' },
  { value: 'fournisseurs', label: 'Fournisseurs', countKey: 'suppliers' },
  { value: 'a-relancer', label: 'À relancer', countKey: 'followUp' },
  { value: 'professionnels', label: 'Professionnels', countKey: 'professional', adaptation: true },
]
