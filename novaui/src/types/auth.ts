export type Role = 'ADMIN' | 'STOCK' | 'VENTE' | 'RH' | 'CRM'

export interface LoginRequest {
  email: string
  password: string
}

export interface Session {
  token: string
  email: string
  role: Role
  roles?: Role[]
  fullName?: string | null
}

export type LoginResponse = Session

export interface UserAccount {
  id: number
  email: string
  fullName: string | null
  role: Role
  roles: Role[]
  lastLoginAt?: string | null
  enabled: boolean
  createdAt: string
}
