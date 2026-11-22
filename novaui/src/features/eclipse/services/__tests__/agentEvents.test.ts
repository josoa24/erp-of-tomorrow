import { parseEventData, toAgentEvent, toPendingAction } from '../agentEvents'

const wireAction = { action_id: 'a1b2', tool: 'record_stock_movement', arguments: { quantity: 50 }, summary: 'Entrée de 50 riz' }
const action = { actionId: 'a1b2', tool: 'record_stock_movement', arguments: { quantity: 50 }, summary: 'Entrée de 50 riz' }

describe('agentEvents', () => {
  it('shouldMapStartTokenAndToolCall', () => {
    expect(toAgentEvent('start', { conversation_id: 'c1' })).toEqual({ type: 'start', conversationId: 'c1' })
    expect(toAgentEvent('token', { text: 'Bon' })).toEqual({ type: 'token', text: 'Bon' })
    expect(toAgentEvent('tool_call', { turn: 1, name: 'get_stock_status', arguments: { risk: 'CRITICAL' } })).toEqual({
      type: 'toolCall',
      name: 'get_stock_status',
      arguments: { risk: 'CRITICAL' },
    })
  })

  it('shouldMapToolResult_withPendingAction', () => {
    const event = toAgentEvent('tool_result', {
      name: 'record_stock_movement',
      success: true,
      status: 'pending_confirmation',
      action: wireAction,
      duration_ms: 3,
    })

    expect(event).toEqual({
      type: 'toolResult',
      name: 'record_stock_movement',
      success: true,
      status: 'pending_confirmation',
      errorCode: undefined,
      action,
    })
  })

  it('shouldMapDone_keepingOnlyValidPendingActions', () => {
    const event = toAgentEvent('done', { response: 'Je l’enregistre ?', pending_actions: [wireAction, { tool: 'x' }] })

    expect(event).toEqual({ type: 'done', response: 'Je l’enregistre ?', pendingActions: [action] })
  })

  it('shouldMapError_withCodeOrType', () => {
    expect(toAgentEvent('error', { type: 'SERVER', code: 'GEMINI_TIMEOUT', message: 'Trop long' })).toEqual({
      type: 'error',
      code: 'GEMINI_TIMEOUT',
      message: 'Trop long',
    })
    expect(toAgentEvent('error', null)).toMatchObject({ code: 'SERVER' })
  })

  it('shouldIgnoreUnknownEvents_andToleratesInvalidJson', () => {
    expect(toAgentEvent('ping', {})).toBeNull()
    expect(parseEventData('{oops')).toBeNull()
    expect(toAgentEvent('token', parseEventData('{oops'))).toEqual({ type: 'token', text: '' })
  })

  it('shouldRejectActionWithoutId', () => {
    expect(toPendingAction({ summary: 'sans id' })).toBeUndefined()
  })
})
