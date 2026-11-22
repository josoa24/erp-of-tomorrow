import { ApiError } from '../../../types/apiError'
import { readSession } from '../../sessionStore'
import { httpGet, httpPost } from '../httpClient'

vi.mock('../../sessionStore', () => ({ readSession: vi.fn() }))

const fetchMock = vi.fn()
const jsonResponse = (status: number, body: unknown) =>
  new Response(JSON.stringify(body), { status, headers: { 'Content-Type': 'application/json' } })

describe('httpClient', () => {
  beforeEach(() => {
    vi.stubGlobal('fetch', fetchMock)
    vi.mocked(readSession).mockReturnValue(null)
  })

  afterEach(() => {
    vi.unstubAllGlobals()
    fetchMock.mockReset()
  })

  it('shouldReturnJson_whenResponseIsOk', async () => {
    fetchMock.mockResolvedValue(jsonResponse(200, { total: 3 }))

    await expect(httpGet('/api/stock/summary')).resolves.toEqual({ total: 3 })
  })

  it('shouldSendBearerToken_whenSessionExists', async () => {
    vi.mocked(readSession).mockReturnValue({ token: 'abc', email: 'a@b.c', role: 'ADMIN' })
    fetchMock.mockResolvedValue(jsonResponse(200, {}))

    await httpPost('/api/x', { a: 1 })

    const [, init] = fetchMock.mock.calls[0]
    expect(init.headers.Authorization).toBe('Bearer abc')
    expect(init.method).toBe('POST')
    expect(init.body).toBe('{"a":1}')
  })

  it('shouldThrowTypedApiError_whenServerReturnsError', async () => {
    fetchMock.mockResolvedValue(jsonResponse(403, { type: 'FORBIDDEN', message: 'Interdit' }))

    await expect(httpGet('/api/rh/summary')).rejects.toMatchObject({
      type: 'forbidden',
      status: 403,
      message: 'Interdit',
    })
  })

  it('shouldUseDefaultMessage_whenErrorBodyIsNotJson', async () => {
    fetchMock.mockResolvedValue(new Response('boom', { status: 500 }))

    await expect(httpGet('/api/x')).rejects.toMatchObject({ type: 'server', status: 500 })
  })

  it('shouldThrowNetworkError_whenFetchFails', async () => {
    fetchMock.mockRejectedValue(new TypeError('Failed to fetch'))

    const error = await httpGet('/api/x').catch((e: unknown) => e)

    expect(error).toBeInstanceOf(ApiError)
    expect(error).toMatchObject({ type: 'network', status: 0 })
  })
})
