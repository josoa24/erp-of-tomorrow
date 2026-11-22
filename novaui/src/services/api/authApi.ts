import type { LoginRequest, LoginResponse, UserAccount } from '../../types/auth'
import { httpGet, httpPost, type RequestOptions } from './httpClient'

export const CURRENT_ACCOUNT_PATH = '/api/auth/users/me'

export const login = (credentials: LoginRequest): Promise<LoginResponse> =>
  httpPost<LoginResponse>('/api/auth/login', credentials)

export const getCurrentAccount = (options: RequestOptions = {}): Promise<UserAccount> =>
  httpGet<UserAccount>(CURRENT_ACCOUNT_PATH, options)
