import { ApiError } from '../../types/apiError'
import { toErrorType } from '../../utils/errorType'
import { readSession } from '../sessionStore'

const BASE_URL = import.meta.env.VITE_API_URL ?? ''
const DEFAULT_MESSAGE = 'Le serveur a renvoyé une erreur.'
const NETWORK_MESSAGE = 'Le serveur est injoignable. Vérifiez votre connexion.'

interface ErrorBody {
  type?: string
  message?: string
}

const toError = async (response: Response): Promise<ApiError> => {
  const body = (await response.json().catch(() => null)) as ErrorBody | null
  const type = toErrorType(response.status, body?.type)
  return new ApiError(type, response.status, body?.message ?? DEFAULT_MESSAGE)
}

const authHeader = (): Record<string, string> => {
  const token = readSession()?.token
  return token ? { Authorization: `Bearer ${token}` } : {}
}

export const request = async <T>(path: string, init: RequestInit = {}): Promise<T> => {
  let response: Response
  try {
    response = await fetch(`${BASE_URL}${path}`, {
      ...init,
      headers: { 'Content-Type': 'application/json', ...authHeader(), ...init.headers },
    })
  } catch {
    throw new ApiError('network', 0, NETWORK_MESSAGE)
  }
  if (!response.ok) throw await toError(response)
  return (await response.json()) as T
}

export const httpGet = <T>(path: string): Promise<T> => request<T>(path)

export const httpPost = <T>(path: string, body: unknown): Promise<T> =>
  request<T>(path, { method: 'POST', body: JSON.stringify(body) })
