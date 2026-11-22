import type { ContactQuery } from '../types/contact'
import type { RelationsFilter } from './relationsSearch'

const FILTER_QUERIES: Record<RelationsFilter, ContactQuery> = {
  clients: { kind: 'CUSTOMER' },
  fournisseurs: { kind: 'SUPPLIER' },
  'a-relancer': { kind: 'ALL', followUp: true },
  professionnels: { kind: 'ALL', professional: true },
}

export const visibleFilter = (
  filter: RelationsFilter | undefined,
  adapted: boolean,
): RelationsFilter | undefined => (filter === 'professionnels' && !adapted ? undefined : filter)

export const toContactQuery = (filter: RelationsFilter | undefined, search: string): ContactQuery => ({
  ...(filter ? FILTER_QUERIES[filter] : { kind: 'ALL' }),
  ...(search !== '' && { search }),
})
