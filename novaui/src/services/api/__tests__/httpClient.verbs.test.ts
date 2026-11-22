import { readSession } from '../../sessionStore'
import { httpDelete, httpGetPage, httpPatch, httpPut } from '../httpClient'

vi.mock('../../sessionStore', () => ({ readSession: vi.fn() }))

const fetchMock = vi.fn()
const jsonResponse = (status: number, body: unknown, headers: Record<string, string> = {}) =>
  new Response(JSON.stringify(body), { status, headers: { 'Content-Type': 'application/json', ...headers } })

describe('httpClient verbs', () => {
  beforeEach(() => {
    vi.stubGlobal('fetch', fetchMock)
    vi.mocked(readSession).mockReturnValue(null)
  })

  afterEach(() => {
    vi.unstubAllGlobals()
    fetchMock.mockReset()
  })

  it('shouldSendPutWithJsonBody', async () => {
    fetchMock.mockResolvedValue(jsonResponse(200, { id: 1 }))

    await expect(httpPut('/api/stock/products/1', { name: 'Riz' })).resolves.toEqual({ id: 1 })

    const [, init] = fetchMock.mock.calls[0]
    expect(init.method).toBe('PUT')
    expect(init.body).toBe('{"name":"Riz"}')
  })

  it('shouldSendPatchWithJsonBody', async () => {
    fetchMock.mockResolvedValue(jsonResponse(200, {}))

    await httpPatch('/api/rh/leaves/4', { status: 'APPROVED' })

    expect(fetchMock.mock.calls[0][1].method).toBe('PATCH')
  })

  it('shouldResolveUndefined_whenDeleteAnswers204', async () => {
    fetchMock.mockResolvedValue(new Response(null, { status: 204 }))

    await expect(httpDelete('/api/crm/contacts/9')).resolves.toBeUndefined()
    expect(fetchMock.mock.calls[0][1].method).toBe('DELETE')
  })

  it('shouldResolveUndefined_whenBodyIsEmpty', async () => {
    fetchMock.mockResolvedValue(new Response('', { status: 200 }))

    await expect(httpPut('/api/x')).resolves.toBeUndefined()
    expect(fetchMock.mock.calls[0][1].body).toBeUndefined()
  })

  it('shouldReadTotalFromHeader_whenGettingAPage', async () => {
    fetchMock.mockResolvedValue(jsonResponse(200, [{ id: 1 }], { 'X-Total-Count': '64' }))

    await expect(httpGetPage('/api/stock/products?page=0')).resolves.toEqual({ items: [{ id: 1 }], total: 64 })
  })

  it('shouldFallBackToItemCount_whenTotalHeaderIsMissing', async () => {
    fetchMock.mockResolvedValue(jsonResponse(200, [{ id: 1 }, { id: 2 }]))

    await expect(httpGetPage('/api/x')).resolves.toMatchObject({ total: 2 })
  })
})
