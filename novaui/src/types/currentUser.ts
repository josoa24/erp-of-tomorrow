import type { Role } from './auth'

export interface CurrentUser {
  email: string
  role: Role
  roleLabel: string
  displayName: string
  initials: string
}
