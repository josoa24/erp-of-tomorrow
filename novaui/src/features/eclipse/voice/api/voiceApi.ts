import { toResponseError, toTransportError } from '../../../../services/api/httpErrors'
import { readSession } from '../../../../services/sessionStore'
import { TRANSCRIBE_PATH, TRANSCRIBE_TIMEOUT_MS } from '../constants/voiceConfig'
import { fileNameFor } from '../services/voiceSupport'
import type { TranscriptionResult } from '../types/voice'

const BASE_URL = import.meta.env.VITE_API_URL ?? ''

const withTimeout = (signal?: AbortSignal): AbortSignal => {
  const timeout = AbortSignal.timeout(TRANSCRIBE_TIMEOUT_MS)
  return signal ? AbortSignal.any([signal, timeout]) : timeout
}

const authHeaders = (): Record<string, string> => {
  const token = readSession()?.token
  return token ? { Authorization: `Bearer ${token}` } : {}
}

export const transcribeAudio = async (
  audio: Blob,
  language: string,
  signal?: AbortSignal,
): Promise<TranscriptionResult> => {
  const form = new FormData()
  form.append('audio', audio, fileNameFor(audio.type))
  form.append('language', language)
  let response: Response
  try {
    response = await fetch(`${BASE_URL}${TRANSCRIBE_PATH}`, {
      method: 'POST',
      body: form,
      headers: authHeaders(),
      signal: withTimeout(signal),
    })
  } catch (error) {
    if (signal?.aborted) throw error
    throw toTransportError(error)
  }
  if (!response.ok) throw await toResponseError(response)
  return (await response.json()) as TranscriptionResult
}
