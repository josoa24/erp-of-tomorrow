import { toResponseError, toTransportError } from '../../../services/api/httpErrors'
import { readSession } from '../../../services/sessionStore'
import { ApiError } from '../../../types/apiError'

const BASE_URL = import.meta.env.VITE_API_URL ?? ''
export const STREAM_OPEN_TIMEOUT_MS = 30_000

const headers = (): Record<string, string> => {
  const token = readSession()?.token
  return {
    'Content-Type': 'application/json',
    Accept: 'text/event-stream',
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
  }
}

const timeoutError = () => new DOMException('Délai dépassé', 'TimeoutError')

export const openEventStream = async (path: string, body: unknown, signal?: AbortSignal): Promise<Response> => {
  const controller = new AbortController()
  let timedOut = false
  const timer = setTimeout(() => {
    timedOut = true
    controller.abort(timeoutError())
  }, STREAM_OPEN_TIMEOUT_MS)
  if (signal?.aborted) controller.abort(signal.reason)
  signal?.addEventListener('abort', () => controller.abort(signal.reason), { once: true })
  try {
    const init = { method: 'POST', body: JSON.stringify(body), signal: controller.signal, headers: headers() }
    const response = await fetch(`${BASE_URL}${path}`, init)
    if (!response.ok) throw await toResponseError(response)
    return response
  } catch (error) {
    if (error instanceof ApiError || signal?.aborted) throw error
    throw toTransportError(timedOut ? timeoutError() : error)
  } finally {
    clearTimeout(timer)
  }
}
