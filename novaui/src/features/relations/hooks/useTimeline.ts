import { useQuery } from '@tanstack/react-query'
import { getTimeline } from '../api/partiesApi'
import { timelineQueryKey } from '../constants/queryKeys'
import type { PartyRef } from '../types/contact'
import type { TimelineEntry } from '../types/interaction'
import { useErrorRedirect } from './useErrorRedirect'

const NO_ENTRY: TimelineEntry[] = []

export const useTimeline = (party: PartyRef | null) => {
  const result = useQuery({
    queryKey: timelineQueryKey(party),
    queryFn: ({ signal }) => (party ? getTimeline(party, { signal }) : Promise.resolve(NO_ENTRY)),
    enabled: party !== null,
  })
  useErrorRedirect(result.error)

  return { entries: result.data ?? NO_ENTRY, loading: result.isPending }
}
