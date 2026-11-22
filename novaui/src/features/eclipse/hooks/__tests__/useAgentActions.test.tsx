import { act, renderHook } from '@testing-library/react'
import { ApiError } from '../../../../types/apiError'
import { cancelAgentAction, confirmAgentAction } from '../../api/agentApi'
import { agentChatReducer, INITIAL_AGENT_CHAT } from '../../services/agentChatReducer'
import type { AgentChatState } from '../../types/agent'
import { useAgentActions } from '../useAgentActions'

vi.mock('../../api/agentApi', () => ({ confirmAgentAction: vi.fn(), cancelAgentAction: vi.fn() }))

const action = { actionId: 'a1', tool: 'create_purchase_order', arguments: {}, summary: 'Bon de commande' }
const proposed = (): AgentChatState => {
  const sent = agentChatReducer(INITIAL_AGENT_CHAT, { type: 'sent', userId: 'u', assistantId: 'r', text: 'Commande' })
  const started = agentChatReducer(sent, { type: 'event', event: { type: 'start', conversationId: 'c1' } })
  return agentChatReducer(started, { type: 'event', event: { type: 'done', response: '', pendingActions: [action] } })
}

const setup = () => {
  const stateRef = { current: proposed() }
  const dispatch = vi.fn()
  const handleError = vi.fn()
  const { result } = renderHook(() => useAgentActions(stateRef, dispatch, handleError))
  return { result, dispatch, handleError }
}

describe('useAgentActions', () => {
  afterEach(() => vi.clearAllMocks())

  it('shouldConfirmOnce_evenOnDoubleClick', async () => {
    vi.mocked(confirmAgentAction).mockResolvedValue({ success: true, message: 'Bon créé' })
    const { result, dispatch } = setup()

    await act(() => Promise.all([result.current.confirm('a1'), result.current.confirm('a1')]))

    expect(confirmAgentAction).toHaveBeenCalledTimes(1)
    expect(confirmAgentAction).toHaveBeenCalledWith('c1', 'a1')
    expect(dispatch).toHaveBeenNthCalledWith(1, { type: 'actionChanged', actionId: 'a1', status: 'confirming' })
    expect(dispatch).toHaveBeenLastCalledWith({ type: 'actionChanged', actionId: 'a1', status: 'done', detail: 'Bon créé' })
  })

  it('shouldCancelAction', async () => {
    vi.mocked(cancelAgentAction).mockResolvedValue({ success: true })
    const { result, dispatch } = setup()

    await act(() => result.current.cancel('a1'))

    expect(cancelAgentAction).toHaveBeenCalledWith('c1', 'a1')
    expect(dispatch).toHaveBeenLastCalledWith(expect.objectContaining({ status: 'cancelled' }))
  })

  it('shouldMarkActionFailed_andOpenErrorPage_whenServerRefuses', async () => {
    const refused = new ApiError('business', 422, 'Stock insuffisant')
    vi.mocked(confirmAgentAction).mockRejectedValue(refused)
    const { result, dispatch, handleError } = setup()

    await act(() => result.current.confirm('a1'))

    expect(dispatch).toHaveBeenLastCalledWith({ type: 'actionChanged', actionId: 'a1', status: 'failed' })
    expect(handleError).toHaveBeenCalledWith(refused)
  })

  it('shouldIgnoreUnknownAction', async () => {
    const { result } = setup()

    await act(() => result.current.confirm('zz'))

    expect(confirmAgentAction).not.toHaveBeenCalled()
  })
})
