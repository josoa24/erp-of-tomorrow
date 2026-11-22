import type { ActionStatus, AgentChatState, AgentMessage, AgentStreamEvent } from '../types/agent'
import { applyStreamEvent, updateLastAssistant } from './agentStreamReducer'

export const INITIAL_AGENT_CHAT: AgentChatState = { conversationId: null, messages: [], actions: {}, busy: false }

export type AgentChatAction =
  | { type: 'sent'; userId: string; assistantId: string; text: string }
  | { type: 'event'; event: AgentStreamEvent }
  | { type: 'ended'; status: 'error' | 'stopped'; error?: string }
  | { type: 'actionChanged'; actionId: string; status: ActionStatus; detail?: string }
  | { type: 'reset' }

const message = (id: string, role: AgentMessage['role'], text: string, status: AgentMessage['status']): AgentMessage => ({
  id,
  role,
  text,
  toolCalls: [],
  actionIds: [],
  status,
})

const endStream = (state: AgentChatState, status: 'error' | 'stopped', error?: string): AgentChatState =>
  updateLastAssistant({ ...state, busy: false }, (last) =>
    last.status === 'streaming' ? { ...last, status, ...(error ? { error } : {}) } : last,
  )

const changeAction = (state: AgentChatState, actionId: string, status: ActionStatus, detail?: string) => {
  const entry = state.actions[actionId]
  if (!entry) return state
  return { ...state, actions: { ...state.actions, [actionId]: { ...entry, status, ...(detail ? { detail } : {}) } } }
}

export const agentChatReducer = (state: AgentChatState, action: AgentChatAction): AgentChatState => {
  switch (action.type) {
    case 'sent':
      return {
        ...state,
        busy: true,
        messages: [
          ...state.messages,
          message(action.userId, 'user', action.text, 'done'),
          message(action.assistantId, 'assistant', '', 'streaming'),
        ],
      }
    case 'event':
      return applyStreamEvent(state, action.event)
    case 'ended':
      return endStream(state, action.status, action.error)
    case 'actionChanged':
      return changeAction(state, action.actionId, action.status, action.detail)
    case 'reset':
      return INITIAL_AGENT_CHAT
  }
}
