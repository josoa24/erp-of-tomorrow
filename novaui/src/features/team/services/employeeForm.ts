import type { ContractType, EmployeeCreateRequest } from '../types/employee'
import { AMOUNT_MESSAGE, REQUIRED_MESSAGE, isBlank, isEmail, optionalText, parseAmount } from './formValidation'

export interface EmployeeFormValues {
  firstName: string
  lastName: string
  email: string
  position: string
  departmentId: string
  contractType: ContractType | ''
  hireDate: string
  baseSalary: string
}

export type EmployeeFormErrors = Partial<Record<keyof EmployeeFormValues, string>>

export const EMPTY_EMPLOYEE_FORM: EmployeeFormValues = {
  firstName: '',
  lastName: '',
  email: '',
  position: '',
  departmentId: '',
  contractType: '',
  hireDate: '',
  baseSalary: '',
}

const required = (value: string): string | undefined => (isBlank(value) ? REQUIRED_MESSAGE : undefined)

const emailError = (value: string): string | undefined =>
  required(value) ?? (isEmail(value) ? undefined : 'Adresse e-mail invalide, par exemple hery@boutique.mg')

const hireDateError = (value: string, today: string): string | undefined =>
  required(value) ?? (value > today ? 'La date d’arrivée ne peut pas dépasser aujourd’hui' : undefined)

const salaryError = (value: string): string | undefined => {
  if (isBlank(value)) return REQUIRED_MESSAGE
  const amount = parseAmount(value)
  return amount === null || amount < 0 ? AMOUNT_MESSAGE : undefined
}

export const validateEmployeeForm = (values: EmployeeFormValues, today: string): EmployeeFormErrors => ({
  firstName: required(values.firstName),
  lastName: required(values.lastName),
  email: emailError(values.email),
  departmentId: required(values.departmentId),
  hireDate: hireDateError(values.hireDate, today),
  baseSalary: salaryError(values.baseSalary),
})

export const toEmployeeRequest = (values: EmployeeFormValues): EmployeeCreateRequest => ({
  firstName: values.firstName.trim(),
  lastName: values.lastName.trim(),
  email: values.email.trim(),
  position: optionalText(values.position),
  departmentId: Number(values.departmentId),
  hireDate: values.hireDate,
  baseSalary: parseAmount(values.baseSalary) ?? 0,
  contractType: values.contractType || undefined,
})
