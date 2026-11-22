import { httpGet } from '../../../../services/api/httpClient'
import { getStockCounts, getStockStatus } from '../stockStatusApi'

vi.mock('../../../../services/api/httpClient', () => ({ httpGet: vi.fn() }))

describe('stockStatusApi', () => {
  afterEach(() => vi.clearAllMocks())

  it('shouldSendSearch_whenSearchIsGiven', async () => {
    const signal = new AbortController().signal
    vi.mocked(httpGet).mockResolvedValue([])

    await getStockStatus('riz blanc', { signal })

    expect(httpGet).toHaveBeenCalledWith('/api/stock/status?search=riz+blanc', { signal })
  })

  it('shouldOmitSearch_whenSearchIsEmpty', async () => {
    vi.mocked(httpGet).mockResolvedValue([])

    await getStockStatus('')

    expect(httpGet).toHaveBeenCalledWith('/api/stock/status', {})
  })

  it('shouldReadCountsFromStatusCounts', async () => {
    const counts = { all: 64, alert: 5, ruptureRisk: 2, expiringSoon: 3 }
    vi.mocked(httpGet).mockResolvedValue(counts)

    const result = await getStockCounts()

    expect(httpGet).toHaveBeenCalledWith('/api/stock/status/counts', {})
    expect(result).toBe(counts)
  })
})
