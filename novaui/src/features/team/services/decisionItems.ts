import type { Advance } from '../types/advance'
import type { DecisionItem } from '../types/decisionItem'
import type { Leave } from '../types/leave'
import { formatAmount, formatPeriod } from './teamFormat'

const UNKNOWN_EMPLOYEE = 'Employé'

export const leaveItem = (leave: Leave): DecisionItem => ({
  id: leave.id,
  title: leave.employeeName ?? UNKNOWN_EMPLOYEE,
  detail: `${formatPeriod(leave.startDate, leave.endDate)} · ${leave.days} j${leave.reason ? ` · ${leave.reason}` : ''}`,
})

export const advanceItem = (advance: Advance): DecisionItem => ({
  id: advance.id,
  title: advance.employeeName ?? UNKNOWN_EMPLOYEE,
  detail: `${formatAmount(advance.amount)}${advance.reason ? ` · ${advance.reason}` : ''}`,
})
