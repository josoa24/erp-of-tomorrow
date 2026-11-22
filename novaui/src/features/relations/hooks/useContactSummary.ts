import { useQuery } from '@tanstack/react-query'
import { getContactSummary } from '../api/contactsApi'
import { SUMMARY_QUERY_KEY } from '../constants/queryKeys'
import { useErrorRedirect } from './useErrorRedirect'

export const useContactSummary = () => {
  const result = useQuery({
    queryKey: SUMMARY_QUERY_KEY,
    queryFn: ({ signal }) => getContactSummary({ signal }),
  })
  useErrorRedirect(result.error)

  return result.data
}
