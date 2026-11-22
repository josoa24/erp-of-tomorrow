import type { TeamDialog } from '../types/teamDialog'
import { AdvanceDecisionsPanel } from './AdvanceDecisionsPanel'
import { AdvancePanel } from './AdvancePanel'
import { EmployeePanel } from './EmployeePanel'
import { LeavePanel } from './LeavePanel'
import { LeaveRequestsPanel } from './LeaveRequestsPanel'

interface TeamDialogsProps {
  dialog: TeamDialog | null
  leaveRequestsOpen: boolean
  onClose: () => void
  onCloseLeaveRequests: () => void
}

export const TeamDialogs = ({ dialog, leaveRequestsOpen, onClose, onCloseLeaveRequests }: TeamDialogsProps) => {
  if (leaveRequestsOpen) return <LeaveRequestsPanel onClose={onCloseLeaveRequests} />
  if (dialog === null) return null
  if (dialog.type === 'employee') return <EmployeePanel onClose={onClose} />
  if (dialog.type === 'leave') return <LeavePanel employee={dialog.employee} onClose={onClose} />
  if (dialog.type === 'advance') return <AdvancePanel employee={dialog.employee} onClose={onClose} />
  if (dialog.type === 'advanceDecisions') return <AdvanceDecisionsPanel employee={dialog.employee} onClose={onClose} />
  return null
}
