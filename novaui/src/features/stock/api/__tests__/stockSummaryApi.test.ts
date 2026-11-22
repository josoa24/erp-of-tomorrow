import { httpGet } from '../../../../services/api/httpClient'
import { getStockSummary } from '../stockSummaryApi'

vi.mock('../../../../services/api/httpClient', () => ({ httpGet: vi.fn() }))

describe('getStockSummary', () => {
  it('shouldReadSummaryThroughTheGateway', async () => {
    const signal = new AbortController().signal
    vi.mocked(httpGet).mockResolvedValue({ totalProducts: 64 })

    await getStockSummary({ signal })

    expect(httpGet).toHaveBeenCalledWith('/api/stock/summary', { signal })
  })
})
