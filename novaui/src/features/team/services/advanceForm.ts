import type { AdvanceCreateRequest } from '../types/advance'
import type { Employee } from '../types/employee'
import { AMOUNT_MESSAGE, REQUIRED_MESSAGE, isBlank, optionalText, parseAmount } from './formValidation'
import { formatAmount } from './teamFormat'

export interface AdvanceFormValues {
  amount: string
  reason: string
}

export const EMPTY_ADVANCE_FORM: AdvanceFormValues = { amount: '', reason: '' }

export const remainingCap = ({ advanceCap, monthAdvance }: Pick<Employee, 'advanceCap' | 'monthAdvance'>): number =>
  Math.max(0, advanceCap - monthAdvance)

export const validateAdvanceAmount = (value: string): string | undefined => {
  if (isBlank(value)) return REQUIRED_MESSAGE
  const amount = parseAmount(value)
  return amount === null || amount <= 0 ? AMOUNT_MESSAGE : undefined
}

export const capHelper = (employee: Pick<Employee, 'advanceCap' | 'monthAdvance'>, value: string): string => {
  const remaining = remainingCap(employee)
  const amount = parseAmount(value)
  const base = `Plafond restant ce mois : ${formatAmount(remaining)} sur ${formatAmount(employee.advanceCap)}`
  return amount !== null && amount > remaining ? `${base}. Ce montant dépasse le plafond restant.` : base
}

export const toAdvanceRequest = (employeeId: number, values: AdvanceFormValues): AdvanceCreateRequest => ({
  employeeId,
  amount: parseAmount(values.amount) ?? 0,
  reason: optionalText(values.reason),
})
