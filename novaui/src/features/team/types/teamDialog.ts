import type { Employee } from './employee'

export type TeamDialog =
  | { type: 'employee' }
  | { type: 'leaveRequests' }
  | { type: 'leave'; employee: Employee }
  | { type: 'advance'; employee: Employee }
  | { type: 'advanceDecisions'; employee: Employee }
