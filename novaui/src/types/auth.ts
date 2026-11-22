export type Role = 'ADMIN' | 'STOCK' | 'VENTE' | 'RH' | 'CRM'

export interface LoginRequest {
  email: string
  password: string
}

export interface Session {
  token: string
  email: string
  role: Role
  fullName?: string | null
}

export type LoginResponse = Session

export interface UserAccount {
  id: number
  email: string
  fullName: string | null
  role: Role
  enabled: boolean
  createdAt: string
}
