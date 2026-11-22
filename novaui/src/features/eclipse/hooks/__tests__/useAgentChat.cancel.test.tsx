import { act, renderHook } from '@testing-library/react'
import { useApiError } from '../../../../hooks/useApiError'
import { streamAgentChat } from '../../api/agentApi'
import { useAgentChat } from '../useAgentChat'

vi.mock('../../api/agentApi', () => ({
  streamAgentChat: vi.fn(),
  deleteAgentConversation: vi.fn(),
  confirmAgentAction: vi.fn(),
  cancelAgentAction: vi.fn(),
}))
vi.mock('../../../../hooks/useApiError', () => ({ useApiError: vi.fn() }))

const handleError = vi.fn()

describe('useAgentChat when sending and stopping', () => {
  beforeEach(() => {
    vi.mocked(useApiError).mockReturnValue(handleError)
    vi.mocked(streamAgentChat).mockImplementation(
      (_request, _onEvent, signal) =>
        new Promise((_, reject) =>
          signal?.addEventListener('abort', () => reject(new DOMException('Aborted', 'AbortError'))),
        ),
    )
  })

  afterEach(() => vi.clearAllMocks())

  it('shouldIgnoreEmptyMessage_andSecondSendWhileStreaming', async () => {
    const { result } = renderHook(() => useAgentChat(vi.fn()))

    await act(() => result.current.send('   '))
    let first: Promise<void> = Promise.resolve()
    act(() => {
      first = result.current.send('Question')
      void result.current.send('Encore')
    })

    expect(streamAgentChat).toHaveBeenCalledTimes(1)
    await act(async () => {
      result.current.stop()
      await first
    })
  })

  it('shouldMarkMessageStopped_whenUserStops', async () => {
    const { result } = renderHook(() => useAgentChat(vi.fn()))

    let pending: Promise<void> = Promise.resolve()
    act(() => {
      pending = result.current.send('Question')
    })
    await act(async () => {
      result.current.stop()
      await pending
    })

    expect(result.current.messages[1].status).toBe('stopped')
    expect(result.current.busy).toBe(false)
    expect(handleError).not.toHaveBeenCalled()
  })
})
