export type Role = 'ADMIN' | 'STOCK' | 'VENTE' | 'RH' | 'CRM'

export interface LoginRequest {
  email: string
  password: string
}

export interface Session {
  token: string
  email: string
  role: Role
}

export type LoginResponse = Session
