import type { AgentChatState, AgentStreamEvent } from '../../types/agent'
import { agentChatReducer, INITIAL_AGENT_CHAT } from '../agentChatReducer'
import { applyStreamEvent } from '../agentStreamReducer'

const action = { actionId: 'a1', tool: 'record_stock_movement', arguments: {}, summary: 'Entrée de 50 riz' }
const sent: AgentChatState = agentChatReducer(INITIAL_AGENT_CHAT, {
  type: 'sent',
  userId: 'u1',
  assistantId: 'a1',
  text: 'Entrée de 50 riz',
})
const play = (events: AgentStreamEvent[], from = sent) => events.reduce(applyStreamEvent, from)
const assistant = (state: AgentChatState) => state.messages[state.messages.length - 1]

describe('applyStreamEvent', () => {
  it('shouldBuildAnswer_fromStartTokensAndDone', () => {
    const state = play([
      { type: 'start', conversationId: 'c1' },
      { type: 'token', text: 'Bon' },
      { type: 'token', text: 'jour' },
      { type: 'done', response: 'Bonjour', pendingActions: [] },
    ])

    expect(state.conversationId).toBe('c1')
    expect(state.busy).toBe(false)
    expect(assistant(state)).toMatchObject({ text: 'Bonjour', status: 'done' })
  })

  it('shouldUseFinalResponse_whenNoTokenWasStreamed', () => {
    expect(assistant(play([{ type: 'done', response: 'Réponse', pendingActions: [] }])).text).toBe('Réponse')
  })

  it('shouldTrackToolCalls_andRegisterProposedAction', () => {
    const state = play([
      { type: 'toolCall', name: 'search_products', arguments: { search: 'riz' } },
      { type: 'toolCall', name: 'record_stock_movement', arguments: {} },
      { type: 'toolResult', name: 'search_products', success: true },
      { type: 'toolResult', name: 'record_stock_movement', success: true, status: 'pending_confirmation', action },
    ])

    expect(assistant(state).toolCalls.map((call) => call.success)).toEqual([true, true])
    expect(assistant(state).toolCalls[1].status).toBe('pending_confirmation')
    expect(assistant(state).actionIds).toEqual(['a1'])
    expect(state.actions.a1).toEqual({ action, status: 'pending' })
  })

  it('shouldMarkActionDone_whenConfirmedInConversation', () => {
    const proposed = play([{ type: 'done', response: 'Je l’enregistre ?', pendingActions: [action] }])

    const state = play(
      [{ type: 'toolResult', name: 'confirm_action', success: true, status: 'executed', action }],
      { ...proposed, messages: [...proposed.messages, ...sent.messages] },
    )

    expect(state.actions.a1.status).toBe('done')
  })

  it('shouldKeepErrorOnMessage_whenStreamReportsError', () => {
    const state = play([{ type: 'error', code: 'GEMINI_TIMEOUT', message: 'Trop long' }])

    expect(state.busy).toBe(false)
    expect(assistant(state)).toMatchObject({ status: 'error', error: 'Trop long' })
  })

  it('shouldIgnoreEvents_whenNoAssistantMessageIsOpen', () => {
    expect(applyStreamEvent(INITIAL_AGENT_CHAT, { type: 'token', text: 'x' })).toEqual(INITIAL_AGENT_CHAT)
  })
})
