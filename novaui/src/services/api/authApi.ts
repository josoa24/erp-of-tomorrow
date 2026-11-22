import type { LoginRequest, LoginResponse } from '../../types/auth'
import { httpPost } from './httpClient'

export const login = (credentials: LoginRequest): Promise<LoginResponse> =>
  httpPost<LoginResponse>('/api/auth/login', credentials)
