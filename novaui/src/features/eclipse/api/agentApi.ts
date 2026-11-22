import { httpDelete, httpPost } from '../../../services/api/httpClient'
import { parseEventData, toAgentEvent } from '../services/agentEvents'
import type { AgentActionResult, AgentStreamEvent } from '../types/agent'
import { openEventStream } from './eventStreamRequest'
import { readSseStream } from './sseStream'

export const AGENT_PATH = '/api/ai/agent'

export interface AgentChatRequest {
  message: string
  conversationId: string | null
}

const conversationPath = (conversationId: string) => `${AGENT_PATH}/conversations/${encodeURIComponent(conversationId)}`

const actionPath = (conversationId: string, actionId: string, verb: 'confirm' | 'cancel') =>
  `${conversationPath(conversationId)}/actions/${encodeURIComponent(actionId)}/${verb}`

export const streamAgentChat = async (
  { message, conversationId }: AgentChatRequest,
  onEvent: (event: AgentStreamEvent) => void,
  signal?: AbortSignal,
): Promise<void> => {
  const body = { message, conversation_id: conversationId }
  const response = await openEventStream(`${AGENT_PATH}/chat/stream`, body, signal)
  await readSseStream(response, ({ event, data }) => {
    const parsed = toAgentEvent(event, parseEventData(data))
    if (parsed) onEvent(parsed)
  })
}

export const confirmAgentAction = (conversationId: string, actionId: string): Promise<AgentActionResult> =>
  httpPost<AgentActionResult>(actionPath(conversationId, actionId, 'confirm'))

export const cancelAgentAction = (conversationId: string, actionId: string): Promise<AgentActionResult> =>
  httpPost<AgentActionResult>(actionPath(conversationId, actionId, 'cancel'))

export const deleteAgentConversation = (conversationId: string): Promise<{ deleted: boolean }> =>
  httpDelete<{ deleted: boolean }>(conversationPath(conversationId))
