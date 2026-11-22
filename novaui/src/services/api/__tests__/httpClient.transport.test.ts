import { readSession } from '../../sessionStore'
import { httpGet } from '../httpClient'

vi.mock('../../sessionStore', () => ({ readSession: vi.fn() }))

const fetchMock = vi.fn()

describe('httpClient transport', () => {
  beforeEach(() => {
    vi.stubGlobal('fetch', fetchMock)
    vi.mocked(readSession).mockReturnValue(null)
  })

  afterEach(() => {
    vi.unstubAllGlobals()
    fetchMock.mockReset()
  })

  it('shouldAttachAnAbortSignal_toEveryRequest', async () => {
    fetchMock.mockResolvedValue(new Response('{}', { status: 200 }))

    await httpGet('/api/x')

    expect(fetchMock.mock.calls[0][1].signal).toBeInstanceOf(AbortSignal)
  })

  it('shouldThrowNetworkTimeout_whenTheServerIsTooSlow', async () => {
    fetchMock.mockRejectedValue(new DOMException('Délai dépassé', 'TimeoutError'))

    await expect(httpGet('/api/x')).rejects.toMatchObject({ type: 'network', status: 0, code: 'TIMEOUT' })
  })

  it('shouldRethrowAbort_whenTheCallerCancels', async () => {
    const controller = new AbortController()
    controller.abort()
    const abort = new DOMException('Annulé', 'AbortError')
    fetchMock.mockRejectedValue(abort)

    await expect(httpGet('/api/x', { signal: controller.signal })).rejects.toBe(abort)
  })

  it('shouldClassifyGatewayUnavailable_asNetwork', async () => {
    fetchMock.mockResolvedValue(new Response(JSON.stringify({ type: 'SERVER' }), { status: 503 }))

    await expect(httpGet('/api/ai/x')).rejects.toMatchObject({ type: 'network', status: 503, code: 'SERVER' })
  })
})
