import type { LeaveCreateRequest } from '../types/leave'
import { REQUIRED_MESSAGE, isBlank, optionalText } from './formValidation'

export interface LeaveFormValues {
  startDate: string
  endDate: string
  reason: string
}

export type LeaveFormErrors = Partial<Record<keyof LeaveFormValues, string>>

export const EMPTY_LEAVE_FORM: LeaveFormValues = { startDate: '', endDate: '', reason: '' }

const endDateError = ({ startDate, endDate }: LeaveFormValues): string | undefined => {
  if (isBlank(endDate)) return REQUIRED_MESSAGE
  return !isBlank(startDate) && endDate < startDate ? 'La date de fin doit suivre la date de début' : undefined
}

export const validateLeaveForm = (values: LeaveFormValues): LeaveFormErrors => ({
  startDate: isBlank(values.startDate) ? REQUIRED_MESSAGE : undefined,
  endDate: endDateError(values),
})

export const toLeaveRequest = (employeeId: number, values: LeaveFormValues): LeaveCreateRequest => ({
  employeeId,
  startDate: values.startDate,
  endDate: values.endDate,
  reason: optionalText(values.reason),
})
