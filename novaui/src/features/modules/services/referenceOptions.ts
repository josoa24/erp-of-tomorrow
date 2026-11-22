import type { ReferenceTarget } from '../types/moduleDefinition'
import type { ReferenceOption } from '../types/moduleRecord'

const text = (value: unknown): string => (typeof value === 'string' ? value : '')

const LABELERS: Record<ReferenceTarget, (item: Record<string, unknown>) => string> = {
  CUSTOMER: (item) => text(item.name),
  SALE: (item) => [text(item.number), text(item.customerName)].filter(Boolean).join(' · '),
  EMPLOYEE: (item) => `${text(item.firstName)} ${text(item.lastName)}`.trim(),
  PRODUCT: (item) => text(item.name),
  SUPPLIER: (item) => text(item.name),
}

export const REFERENCE_LABELS: Record<ReferenceTarget, string> = {
  CUSTOMER: 'Relations',
  SALE: 'Ventes',
  EMPLOYEE: 'Équipe',
  PRODUCT: 'Stock',
  SUPPLIER: 'Stock',
}

export const REFERENCE_NOUNS: Record<ReferenceTarget, string> = {
  CUSTOMER: 'un client',
  SALE: 'une vente',
  EMPLOYEE: 'un employé',
  PRODUCT: 'un produit',
  SUPPLIER: 'un fournisseur',
}

export const toReferenceOptions = (target: ReferenceTarget, items: Record<string, unknown>[]): ReferenceOption[] =>
  items
    .filter((item) => typeof item.id === 'number')
    .map((item) => ({ id: item.id as number, label: LABELERS[target](item) }))
