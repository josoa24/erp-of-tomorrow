import { keepPreviousData, useQuery } from '@tanstack/react-query'
import { getContactCounts } from '../api/contactsApi'
import { countsQueryKey } from '../constants/queryKeys'
import { useErrorRedirect } from './useErrorRedirect'

export const useContactCounts = (search: string) => {
  const result = useQuery({
    queryKey: countsQueryKey(search),
    queryFn: ({ signal }) => getContactCounts(search, { signal }),
    placeholderData: keepPreviousData,
  })
  useErrorRedirect(result.error)

  return result.data
}
