import { readSession } from '../../../../services/sessionStore'
import { openEventStream, STREAM_OPEN_TIMEOUT_MS } from '../eventStreamRequest'

vi.mock('../../../../services/sessionStore', () => ({ readSession: vi.fn() }))

const fetchMock = vi.fn()

describe('openEventStream', () => {
  beforeEach(() => {
    vi.stubGlobal('fetch', fetchMock)
    vi.mocked(readSession).mockReturnValue({ token: 'jeton', email: 'hery.rabe@boutique.mg', role: 'ADMIN' })
  })
  afterEach(() => {
    vi.unstubAllGlobals()
    vi.useRealTimers()
    fetchMock.mockReset()
  })

  it('shouldPostJsonWithBearerToken_andReturnTheOpenResponse', async () => {
    const response = new Response('event: start\ndata: {}\n\n', { status: 200 })
    fetchMock.mockResolvedValue(response)

    await expect(openEventStream('/api/ai/agent/chat/stream', { message: 'Bonjour' })).resolves.toBe(response)

    const [path, init] = fetchMock.mock.calls[0]
    expect(path).toBe('/api/ai/agent/chat/stream')
    expect(init).toMatchObject({ method: 'POST', body: '{"message":"Bonjour"}' })
    expect(init.headers).toMatchObject({ Authorization: 'Bearer jeton', Accept: 'text/event-stream' })
  })

  it('shouldThrowTypedError_whenServerRefuses', async () => {
    fetchMock.mockResolvedValue(
      new Response(JSON.stringify({ type: 'RATE_LIMIT', message: 'Trop de requêtes' }), { status: 429 }),
    )

    await expect(openEventStream('/x', {})).rejects.toMatchObject({ status: 429, message: 'Trop de requêtes' })
  })

  it('shouldReportNetworkError_whenServerIsUnreachable', async () => {
    fetchMock.mockRejectedValue(new TypeError('Failed to fetch'))

    await expect(openEventStream('/x', {})).rejects.toMatchObject({ type: 'network', status: 0 })
  })

  it('shouldReportTimeout_whenHeadersNeverArrive', async () => {
    vi.useFakeTimers()
    fetchMock.mockImplementation(
      (_: string, init: RequestInit) =>
        new Promise((_resolve, reject) => init.signal?.addEventListener('abort', () => reject(init.signal?.reason))),
    )

    const pending = expect(openEventStream('/x', {})).rejects.toMatchObject({ type: 'network', code: 'TIMEOUT' })
    await vi.advanceTimersByTimeAsync(STREAM_OPEN_TIMEOUT_MS)

    await pending
  })

  it('shouldKeepAbort_whenCallerCancels', async () => {
    const controller = new AbortController()
    fetchMock.mockImplementation(
      (_: string, init: RequestInit) =>
        new Promise((_resolve, reject) => init.signal?.addEventListener('abort', () => reject(init.signal?.reason))),
    )

    const pending = openEventStream('/x', {}, controller.signal)
    controller.abort()

    await expect(pending).rejects.toMatchObject({ name: 'AbortError' })
  })
})
