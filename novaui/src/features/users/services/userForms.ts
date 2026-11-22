import { isBlank, isEmail, REQUIRED_MESSAGE } from '../../team/services/formValidation'
import type { Role } from '../../../types/auth'
import { sortRoles } from '../constants/roles'
import type { CreateUserRequest } from '../types/user'
import { MIN_PASSWORD_LENGTH } from './passwordGenerator'

export const ROLES_REQUIRED_MESSAGE = 'Choisissez au moins un rôle'
export const EMAIL_MESSAGE = 'Saisissez une adresse e-mail valide'
export const PASSWORD_MESSAGE = `Au moins ${MIN_PASSWORD_LENGTH} caractères`

export interface UserFormValues {
  fullName: string
  email: string
  roles: Role[]
  password: string
}

export type UserFormErrors = Partial<Record<keyof UserFormValues, string>>

export const EMPTY_USER_FORM: UserFormValues = { fullName: '', email: '', roles: [], password: '' }

export const validatePassword = (password: string): string | undefined =>
  password.length >= MIN_PASSWORD_LENGTH ? undefined : PASSWORD_MESSAGE

export const validateUserForm = (values: UserFormValues): UserFormErrors => ({
  email: isBlank(values.email) ? REQUIRED_MESSAGE : isEmail(values.email) ? undefined : EMAIL_MESSAGE,
  roles: values.roles.length > 0 ? undefined : ROLES_REQUIRED_MESSAGE,
  password: validatePassword(values.password),
})

export const toCreateUserRequest = (values: UserFormValues): CreateUserRequest => ({
  email: values.email.trim(),
  password: values.password,
  roles: sortRoles(values.roles),
  ...(isBlank(values.fullName) ? {} : { fullName: values.fullName.trim() }),
})
