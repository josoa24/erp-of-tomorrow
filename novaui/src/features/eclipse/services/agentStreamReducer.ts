import type {
  ActionStatus,
  AgentChatState,
  AgentMessage,
  AgentStreamEvent,
  AgentToolCall,
  PendingAction,
  ToolCallStatus,
} from '../types/agent'

type ToolResultEvent = Extract<AgentStreamEvent, { type: 'toolResult' }>

const ACTION_STATUS_BY_TOOL_STATUS: Partial<Record<ToolCallStatus, ActionStatus>> = {
  executed: 'done',
  failed: 'failed',
  cancelled: 'cancelled',
}

export const updateLastAssistant = (
  state: AgentChatState,
  update: (message: AgentMessage) => AgentMessage,
): AgentChatState => {
  const last = state.messages.at(-1)
  if (last?.role !== 'assistant') return state
  return { ...state, messages: [...state.messages.slice(0, -1), update(last)] }
}

const addPendingAction = (state: AgentChatState, action: PendingAction): AgentChatState => {
  if (state.actions[action.actionId]) return state
  const withEntry = { ...state, actions: { ...state.actions, [action.actionId]: { action, status: 'pending' as const } } }
  return updateLastAssistant(withEntry, (message) =>
    message.actionIds.includes(action.actionId) ? message : { ...message, actionIds: [...message.actionIds, action.actionId] },
  )
}

const setActionStatus = (state: AgentChatState, actionId: string, status: ActionStatus): AgentChatState => {
  const entry = state.actions[actionId]
  return entry ? { ...state, actions: { ...state.actions, [actionId]: { ...entry, status } } } : state
}

const completeToolCall = (calls: AgentToolCall[], event: ToolResultEvent) => {
  const index = calls.findIndex((call) => call.success === undefined)
  if (index < 0) return calls
  const done = { ...calls[index], success: event.success, status: event.status, errorCode: event.errorCode }
  return [...calls.slice(0, index), done, ...calls.slice(index + 1)]
}

const applyToolResult = (state: AgentChatState, event: ToolResultEvent) => {
  const next = updateLastAssistant(state, (message) => ({ ...message, toolCalls: completeToolCall(message.toolCalls, event) }))
  if (!event.action) return next
  if (event.status === 'pending_confirmation') return addPendingAction(next, event.action)
  const status = event.status && ACTION_STATUS_BY_TOOL_STATUS[event.status]
  return status ? setActionStatus(next, event.action.actionId, status) : next
}

export const applyStreamEvent = (state: AgentChatState, event: AgentStreamEvent): AgentChatState => {
  switch (event.type) {
    case 'start':
      return { ...state, conversationId: event.conversationId || state.conversationId }
    case 'token':
      return updateLastAssistant(state, (message) => ({ ...message, text: message.text + event.text }))
    case 'toolCall':
      return updateLastAssistant(state, (message) => ({
        ...message,
        toolCalls: [...message.toolCalls, { name: event.name, arguments: event.arguments }],
      }))
    case 'toolResult':
      return applyToolResult(state, event)
    case 'ui':
      return updateLastAssistant(state, (message) => ({ ...message, blocks: [...message.blocks, ...event.blocks] }))
    case 'done': {
      const finished = updateLastAssistant({ ...state, busy: false }, (message) => ({
        ...message,
        status: 'done',
        text: message.text || event.response,
      }))
      return event.pendingActions.reduce(addPendingAction, finished)
    }
    case 'error':
      return updateLastAssistant({ ...state, busy: false }, (message) => ({ ...message, status: 'error', error: event.message }))
  }
}
