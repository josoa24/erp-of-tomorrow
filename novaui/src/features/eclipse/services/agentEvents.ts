import type { AgentStreamEvent, PendingAction, ToolCallStatus } from '../types/agent'
import { toUiBlocks } from './uiBlockParser'

type Wire = Record<string, unknown>

const TOOL_STATUSES: ToolCallStatus[] = ['pending_confirmation', 'executed', 'failed', 'cancelled']

const asObject = (value: unknown): Wire =>
  typeof value === 'object' && value !== null && !Array.isArray(value) ? (value as Wire) : {}

const asString = (value: unknown, fallback = ''): string => (typeof value === 'string' ? value : fallback)

const asStatus = (value: unknown): ToolCallStatus | undefined =>
  TOOL_STATUSES.find((status) => status === value)

export const parseEventData = (data: string): unknown => {
  try {
    return JSON.parse(data)
  } catch {
    return null
  }
}

export const toPendingAction = (wire: unknown): PendingAction | undefined => {
  const value = asObject(wire)
  const actionId = asString(value.action_id)
  if (!actionId) return undefined
  return { actionId, tool: asString(value.tool), arguments: asObject(value.arguments), summary: asString(value.summary) }
}

const toPendingActions = (wire: unknown): PendingAction[] =>
  (Array.isArray(wire) ? wire : []).flatMap((item) => toPendingAction(item) ?? [])

export const toAgentEvent = (name: string, data: unknown): AgentStreamEvent | null => {
  const value = asObject(data)
  switch (name) {
    case 'start':
      return { type: 'start', conversationId: asString(value.conversation_id) }
    case 'token':
      return { type: 'token', text: asString(value.text) }
    case 'tool_call':
      return { type: 'toolCall', name: asString(value.name), arguments: asObject(value.arguments) }
    case 'tool_result':
      return {
        type: 'toolResult',
        name: asString(value.name),
        success: value.success === true,
        status: asStatus(value.status),
        errorCode: asString(value.error_code) || undefined,
        action: toPendingAction(value.action),
      }
    case 'ui':
      return { type: 'ui', tool: asString(value.tool), blocks: toUiBlocks(value.blocks) }
    case 'done':
      return { type: 'done', response: asString(value.response), pendingActions: toPendingActions(value.pending_actions) }
    case 'error':
      return {
        type: 'error',
        code: asString(value.code, asString(value.type, 'SERVER')),
        message: asString(value.message, 'Eclipse a rencontré une erreur.'),
      }
    default:
      return null
  }
}
