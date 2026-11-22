import { httpDelete, httpPost } from '../../../../services/api/httpClient'
import type { AgentStreamEvent } from '../../types/agent'
import { cancelAgentAction, confirmAgentAction, deleteAgentConversation, streamAgentChat } from '../agentApi'
import { openEventStream } from '../eventStreamRequest'

vi.mock('../eventStreamRequest', () => ({ openEventStream: vi.fn() }))
vi.mock('../../../../services/api/httpClient', () => ({ httpPost: vi.fn(), httpDelete: vi.fn() }))

const sse = (body: string) => new Response(body, { headers: { 'Content-Type': 'text/event-stream' } })

describe('agentApi', () => {
  afterEach(() => vi.clearAllMocks())

  it('shouldPostQuestion_andForwardTypedEvents', async () => {
    vi.mocked(openEventStream).mockResolvedValue(
      sse('event: start\ndata: {"conversation_id":"c1"}\n\nevent: ping\ndata: {}\n\nevent: token\ndata: {"text":"Oui"}\n\n'),
    )
    const events: AgentStreamEvent[] = []
    const signal = new AbortController().signal

    await streamAgentChat({ message: 'Rupture ?', conversationId: null }, (event) => events.push(event), signal)

    expect(openEventStream).toHaveBeenCalledWith(
      '/api/ai/agent/chat/stream',
      { message: 'Rupture ?', conversation_id: null },
      signal,
    )
    expect(events).toEqual([
      { type: 'start', conversationId: 'c1' },
      { type: 'token', text: 'Oui' },
    ])
  })

  it('shouldConfirmAndCancel_onEncodedActionPaths', async () => {
    vi.mocked(httpPost).mockResolvedValue({ success: true })

    await confirmAgentAction('c 1', 'ab12')
    await cancelAgentAction('c1', 'ab12')

    expect(httpPost).toHaveBeenNthCalledWith(1, '/api/ai/agent/conversations/c%201/actions/ab12/confirm')
    expect(httpPost).toHaveBeenNthCalledWith(2, '/api/ai/agent/conversations/c1/actions/ab12/cancel')
  })

  it('shouldDeleteConversation', async () => {
    vi.mocked(httpDelete).mockResolvedValue({ deleted: true })

    await expect(deleteAgentConversation('c1')).resolves.toEqual({ deleted: true })
    expect(httpDelete).toHaveBeenCalledWith('/api/ai/agent/conversations/c1')
  })
})
