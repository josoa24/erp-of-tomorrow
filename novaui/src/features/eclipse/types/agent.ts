import type { UiBlock } from './uiBlock'

export type ToolCallStatus ='pending_confirmation' | 'executed' | 'failed' | 'cancelled'

export interface AgentToolCall {
  name: string
  arguments: Record<string, unknown>
  success?: boolean
  status?: ToolCallStatus
  errorCode?: string
}

export interface PendingAction {
  actionId: string
  tool: string
  arguments: Record<string, unknown>
  summary: string
}

export type AgentStreamEvent =
  | { type: 'start'; conversationId: string }
  | { type: 'token'; text: string }
  | { type: 'toolCall'; name: string; arguments: Record<string, unknown> }
  | {
      type: 'toolResult'
      name: string
      success: boolean
      status?: ToolCallStatus
      errorCode?: string
      action?: PendingAction
    }
  | { type: 'ui'; tool: string; blocks: UiBlock[] }
  | { type: 'done'; response: string; pendingActions: PendingAction[] }
  | { type: 'error'; code: string; message: string }

export type AgentMessageStatus = 'streaming' | 'done' | 'error' | 'stopped'

export interface AgentMessage {
  id: string
  role: 'user' | 'assistant'
  text: string
  toolCalls: AgentToolCall[]
  actionIds: string[]
  blocks: UiBlock[]
  status: AgentMessageStatus
  error?: string
}

export type ActionStatus = 'pending' | 'confirming' | 'cancelling' | 'done' | 'cancelled' | 'failed' | 'refused' | 'expired'

export interface ActionEntry {
  action: PendingAction
  status: ActionStatus
  detail?: string
  code?: string
}

export interface AgentChatState {
  conversationId: string | null
  messages: AgentMessage[]
  actions: Record<string, ActionEntry>
  busy: boolean
}

export interface AgentActionResult {
  success?: boolean
  message?: string
  [key: string]: unknown
}
