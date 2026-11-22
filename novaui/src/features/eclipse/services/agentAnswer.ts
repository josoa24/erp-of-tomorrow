import type { AgentStreamEvent } from '../types/agent'
import { streamErrorToApiError } from './eclipseAvailability'

export const nextAnswer = (current: string, event: AgentStreamEvent): string => {
  if (event.type === 'token') return current + event.text
  if (event.type === 'done') return event.response || current
  if (event.type === 'error') throw streamErrorToApiError(event)
  return current
}
