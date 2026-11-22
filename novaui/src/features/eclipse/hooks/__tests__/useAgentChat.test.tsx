import { act, renderHook } from '@testing-library/react'
import { useApiError } from '../../../../hooks/useApiError'
import { ApiError } from '../../../../types/apiError'
import { deleteAgentConversation, streamAgentChat } from '../../api/agentApi'
import { useAgentChat } from '../useAgentChat'

vi.mock('../../api/agentApi', () => ({
  streamAgentChat: vi.fn(),
  deleteAgentConversation: vi.fn().mockResolvedValue({ deleted: true }),
  confirmAgentAction: vi.fn(),
  cancelAgentAction: vi.fn(),
}))
vi.mock('../../../../hooks/useApiError', () => ({ useApiError: vi.fn() }))

const handleError = vi.fn()
const onUnavailable = vi.fn()
const answering = (text: string) =>
  vi.mocked(streamAgentChat).mockImplementation(async (_request, onEvent) => {
    onEvent({ type: 'start', conversationId: 'c1' })
    onEvent({ type: 'token', text })
    onEvent({ type: 'done', response: text, pendingActions: [] })
  })

const renderChat = () => renderHook(() => useAgentChat(onUnavailable))

describe('useAgentChat', () => {
  beforeEach(() => vi.mocked(useApiError).mockReturnValue(handleError))
  afterEach(() => vi.clearAllMocks())

  it('shouldStreamAnswer_withScreenContext_andReuseConversationId', async () => {
    answering('Deux produits critiques')
    const { result } = renderChat()

    await act(() => result.current.send('  Rupture ?  ', 'Écran : Stock'))
    await act(() => result.current.send('Et la semaine dernière ?'))

    expect(result.current.messages[0]).toMatchObject({ role: 'user', text: 'Rupture ?' })
    expect(result.current.messages[1]).toMatchObject({ role: 'assistant', text: 'Deux produits critiques', status: 'done' })
    expect(vi.mocked(streamAgentChat).mock.calls[0][0]).toEqual({ message: 'Écran : Stock\nRupture ?', conversationId: null })
    expect(vi.mocked(streamAgentChat).mock.calls[1][0].conversationId).toBe('c1')
    expect(result.current.busy).toBe(false)
  })

  it('shouldReportUnavailability_whenGeminiIsDown', async () => {
    vi.mocked(streamAgentChat).mockRejectedValue(new ApiError('network', 503, 'Gemini indisponible'))
    const { result } = renderChat()

    await act(() => result.current.send('Question'))

    expect(onUnavailable).toHaveBeenCalledWith('unavailable')
    expect(handleError).not.toHaveBeenCalled()
    expect(result.current.busy).toBe(false)
  })

  it('shouldReportRateLimit_whenTheStreamSaysSo', async () => {
    vi.mocked(streamAgentChat).mockImplementation(async (_request, onEvent) => {
      onEvent({ type: 'error', code: 'GEMINI_RATE_LIMIT', message: 'Quota Gemini atteint' })
    })
    const { result } = renderChat()

    await act(() => result.current.send('Question'))

    expect(onUnavailable).toHaveBeenCalledWith('rateLimited')
    expect(handleError).not.toHaveBeenCalled()
  })

  it('shouldOpenErrorPage_whenSessionIsRejected', async () => {
    const expired = new ApiError('unauthorized', 401, 'Expirée')
    vi.mocked(streamAgentChat).mockRejectedValue(expired)
    const { result } = renderChat()

    await act(() => result.current.send('Question'))

    expect(handleError).toHaveBeenCalledWith(expired)
    expect(onUnavailable).not.toHaveBeenCalled()
  })

  it('shouldOpenErrorPage_whenTheStreamEndsWithoutAnswer', async () => {
    vi.mocked(streamAgentChat).mockImplementation(async (_request, onEvent) => {
      onEvent({ type: 'token', text: 'Deux' })
    })
    const { result } = renderChat()

    await act(() => result.current.send('Question'))

    expect(handleError).toHaveBeenCalledWith(expect.objectContaining({ type: 'network', status: 0 }))
  })

  it('shouldDeleteServerConversation_andClearState_whenReset', async () => {
    answering('ok')
    const { result } = renderChat()
    await act(() => result.current.send('Question'))

    act(() => result.current.reset())

    expect(deleteAgentConversation).toHaveBeenCalledWith('c1')
    expect(result.current.messages).toEqual([])
    expect(result.current.conversationId).toBeNull()
  })
})
