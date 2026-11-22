import { ApiError } from '../../../types/apiError'
import { isAbortError } from '../services/abortError'
import { createSseParser, type SseMessage } from '../services/sseParser'

export const INTERRUPTED_MESSAGE = 'La connexion avec le serveur a été interrompue pendant la réponse.'

export const readSseStream = async (response: Response, onMessage: (message: SseMessage) => void): Promise<void> => {
  if (!response.body) throw new ApiError('server', response.status, 'Le serveur a renvoyé une réponse vide.')
  const reader = response.body.getReader()
  const decoder = new TextDecoder()
  const parser = createSseParser(onMessage)
  try {
    for (;;) {
      const { value, done } = await reader.read()
      if (done) break
      parser.push(decoder.decode(value, { stream: true }))
    }
  } catch (error) {
    if (isAbortError(error)) throw error
    throw new ApiError('network', 0, INTERRUPTED_MESSAGE)
  }
  parser.push(decoder.decode())
  parser.flush()
}
