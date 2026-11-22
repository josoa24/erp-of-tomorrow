import { agentChatReducer, INITIAL_AGENT_CHAT } from '../agentChatReducer'

const action = { actionId: 'a1', tool: 'create_purchase_order', arguments: {}, summary: 'Bon de commande' }
const sent = agentChatReducer(INITIAL_AGENT_CHAT, { type: 'sent', userId: 'u1', assistantId: 'r1', text: 'Commande' })

describe('agentChatReducer', () => {
  it('shouldAddUserAndStreamingAssistantMessages_whenSent', () => {
    expect(sent.busy).toBe(true)
    expect(sent.messages.map((m) => [m.role, m.status, m.text])).toEqual([
      ['user', 'done', 'Commande'],
      ['assistant', 'streaming', ''],
    ])
  })

  it('shouldStopStreamingMessage_whenEnded', () => {
    const stopped = agentChatReducer(sent, { type: 'ended', status: 'stopped' })
    const failed = agentChatReducer(sent, { type: 'ended', status: 'error', error: 'Coupure' })

    expect(stopped.busy).toBe(false)
    expect(stopped.messages[1].status).toBe('stopped')
    expect(failed.messages[1]).toMatchObject({ status: 'error', error: 'Coupure' })
  })

  it('shouldNotOverrideFinishedMessage_whenEndedAfterDone', () => {
    const done = agentChatReducer(sent, { type: 'event', event: { type: 'done', response: 'ok', pendingActions: [] } })

    const ended = agentChatReducer(done, { type: 'ended', status: 'error', error: 'Interrompue' })

    expect(ended.messages[1].status).toBe('done')
  })

  it('shouldUpdateActionStatus_andIgnoreUnknownAction', () => {
    const proposed = agentChatReducer(sent, { type: 'event', event: { type: 'done', response: '', pendingActions: [action] } })

    const confirmed = agentChatReducer(proposed, { type: 'actionChanged', actionId: 'a1', status: 'done', detail: 'Créé' })
    const unknown = agentChatReducer(proposed, { type: 'actionChanged', actionId: 'zz', status: 'done' })

    expect(confirmed.actions.a1).toEqual({ action, status: 'done', detail: 'Créé' })
    expect(unknown).toBe(proposed)
  })

  it('shouldStartOver_whenReset', () => {
    expect(agentChatReducer(sent, { type: 'reset' })).toEqual(INITIAL_AGENT_CHAT)
  })
})
