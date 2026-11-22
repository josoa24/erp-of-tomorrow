import type { Role, UserAccount } from '../../../types/auth'

export interface ManagedUser extends UserAccount {
  lastLoginAt: string | null
}

export interface UserCounts {
  all: number
  active: number
  disabled: number
  admins: number
}

export type UsersFilterKey = 'tous' | 'actifs' | 'desactives'

export interface UsersSearch {
  filtre?: UsersFilterKey
  role?: Role
  recherche?: string
  page?: number
}

export interface UsersListQuery {
  search?: string
  role?: Role
  enabled?: boolean
  page: number
  size: number
}

export interface CreateUserRequest {
  email: string
  password: string
  role: Role
  fullName?: string
}
