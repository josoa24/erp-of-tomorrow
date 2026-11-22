import type { Role } from './auth'

export interface CurrentUser {
  email: string
  role: Role
  roles: Role[]
  roleLabel: string
  roleLabels: string[]
  displayName: string
  initials: string
}
