import { readSession } from '../sessionStore'
import { toResponseError, toTransportError } from './httpErrors'
import { parseBody, readTotalCount } from './httpResponse'

const BASE_URL = import.meta.env.VITE_API_URL ?? ''
export const REQUEST_TIMEOUT_MS = 15_000

export interface RequestOptions {
  signal?: AbortSignal
}

export interface ResponseWithHeaders<T> {
  data: T
  headers: Headers
}

export interface Page<T> {
  items: T[]
  total: number
}

const authHeader = (): Record<string, string> => {
  const token = readSession()?.token
  return token ? { Authorization: `Bearer ${token}` } : {}
}

const withTimeout = (signal?: AbortSignal | null): AbortSignal => {
  const timeout = AbortSignal.timeout(REQUEST_TIMEOUT_MS)
  return signal ? AbortSignal.any([signal, timeout]) : timeout
}

const send = async (path: string, init: RequestInit): Promise<Response> => {
  try {
    return await fetch(`${BASE_URL}${path}`, {
      ...init,
      signal: withTimeout(init.signal),
      headers: { 'Content-Type': 'application/json', ...authHeader(), ...init.headers },
    })
  } catch (error) {
    if (init.signal?.aborted) throw error
    throw toTransportError(error)
  }
}

export const requestWithHeaders = async <T>(
  path: string,
  init: RequestInit = {},
): Promise<ResponseWithHeaders<T>> => {
  const response = await send(path, init)
  if (!response.ok) throw await toResponseError(response)
  return { data: await parseBody<T>(response), headers: response.headers }
}

export const request = async <T>(path: string, init: RequestInit = {}): Promise<T> =>
  (await requestWithHeaders<T>(path, init)).data

const withBody = (method: string, body: unknown, options: RequestOptions): RequestInit => ({
  method,
  body: body === undefined ? undefined : JSON.stringify(body),
  signal: options.signal,
})

export const httpGet = <T>(path: string, options: RequestOptions = {}): Promise<T> =>
  request<T>(path, { signal: options.signal })

export const httpGetPage = async <T>(path: string, options: RequestOptions = {}): Promise<Page<T>> => {
  const { data, headers } = await requestWithHeaders<T[]>(path, { signal: options.signal })
  return { items: data, total: readTotalCount(headers) ?? data.length }
}

export const httpPost = <T>(path: string, body?: unknown, options: RequestOptions = {}): Promise<T> =>
  request<T>(path, withBody('POST', body, options))

export const httpPut = <T>(path: string, body?: unknown, options: RequestOptions = {}): Promise<T> =>
  request<T>(path, withBody('PUT', body, options))

export const httpPatch = <T>(path: string, body?: unknown, options: RequestOptions = {}): Promise<T> =>
  request<T>(path, withBody('PATCH', body, options))

export const httpDelete = <T = void>(path: string, options: RequestOptions = {}): Promise<T> =>
  request<T>(path, { method: 'DELETE', signal: options.signal })
