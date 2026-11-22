import type { ContactQuery, PartyRef } from '../types/contact'

export const RELATIONS_QUERY_KEY = ['relations'] as const

export const contactsQueryKey = (query: ContactQuery) => [...RELATIONS_QUERY_KEY, 'contacts', query] as const

export const countsQueryKey = (search: string) => [...RELATIONS_QUERY_KEY, 'counts', search] as const

export const SUMMARY_QUERY_KEY = [...RELATIONS_QUERY_KEY, 'summary'] as const

export const timelineQueryKey = (party: PartyRef | null) =>
  [...RELATIONS_QUERY_KEY, 'timeline', party?.kind ?? null, party?.partyId ?? null] as const
