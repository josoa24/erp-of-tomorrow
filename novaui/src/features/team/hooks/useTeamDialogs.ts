import { useCallback, useState } from 'react'
import type { Employee } from '../types/employee'
import type { TeamDialog } from '../types/teamDialog'

export const useTeamDialogs = (onLeaveRequestsChange: (open: boolean) => void) => {
  const [dialog, setDialog] = useState<TeamDialog | null>(null)

  const openEmployee = useCallback(() => setDialog({ type: 'employee' }), [])
  const openLeave = useCallback((employee: Employee) => setDialog({ type: 'leave', employee }), [])
  const openAdvance = useCallback((employee: Employee) => setDialog({ type: 'advance', employee }), [])
  const openAdvanceDecisions = useCallback(
    (employee: Employee) => setDialog({ type: 'advanceDecisions', employee }),
    [],
  )
  const close = useCallback(() => setDialog(null), [])
  const closeLeaveRequests = useCallback(() => onLeaveRequestsChange(false), [onLeaveRequestsChange])

  return { dialog, openEmployee, openLeave, openAdvance, openAdvanceDecisions, close, closeLeaveRequests }
}
