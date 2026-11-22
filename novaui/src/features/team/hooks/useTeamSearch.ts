import { useNavigate, useSearch } from '@tanstack/react-router'
import { useCallback } from 'react'
import { LEAVE_REQUESTS_VIEW, validateTeamSearch, type TeamFilter } from '../services/teamSearch'

export const useTeamSearch = () => {
  const navigate = useNavigate()
  const { filtre, vue } = useSearch({ strict: false, select: validateTeamSearch })

  const setFilter = useCallback(
    (filter: TeamFilter) => {
      void navigate({
        to: '.',
        replace: true,
        search: (previous) => ({ ...previous, filtre: filter === 'tous' ? undefined : filter }),
      })
    },
    [navigate],
  )

  const setLeaveRequestsOpen = useCallback(
    (open: boolean) => {
      void navigate({ to: '.', search: (previous) => ({ ...previous, vue: open ? LEAVE_REQUESTS_VIEW : undefined }) })
    },
    [navigate],
  )

  return { filtre, leaveRequestsOpen: vue === LEAVE_REQUESTS_VIEW, setFilter, setLeaveRequestsOpen }
}
