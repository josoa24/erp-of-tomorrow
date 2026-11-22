import { ROLE_LABELS } from '../../../constants/roleLabels'
import type { ComboboxOption } from '../../../components/ui/combobox-types'
import type { Role } from '../../../types/auth'

export const ROLES: readonly Role[] = ['ADMIN', 'STOCK', 'VENTE', 'RH', 'CRM']

export const ROLE_OPTIONS: ComboboxOption[] = ROLES.map((role) => ({ value: role, label: ROLE_LABELS[role] }))

export const isRole = (value: unknown): value is Role => ROLES.some((role) => role === value)

export const roleOption = (role: Role | undefined): ComboboxOption | null =>
  ROLE_OPTIONS.find((option) => option.value === role) ?? null
