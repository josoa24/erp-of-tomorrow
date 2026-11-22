import { keepPreviousData, useQuery } from '@tanstack/react-query'
import { getContacts } from '../api/contactsApi'
import { contactsQueryKey } from '../constants/queryKeys'
import type { Contact, ContactQuery } from '../types/contact'
import { useErrorRedirect } from './useErrorRedirect'

const NO_CONTACT: Contact[] = []

export const useContacts = (query: ContactQuery) => {
  const result = useQuery({
    queryKey: contactsQueryKey(query),
    queryFn: ({ signal }) => getContacts(query, { signal }),
    placeholderData: keepPreviousData,
  })
  useErrorRedirect(result.error)

  return { contacts: result.data ?? NO_CONTACT, loading: result.isPending, refreshing: result.isPlaceholderData }
}
