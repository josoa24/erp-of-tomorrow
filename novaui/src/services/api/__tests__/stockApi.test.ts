import { httpGet } from '../httpClient'
import { getStockStatus, getStockSummary } from '../stockApi'

vi.mock('../httpClient', () => ({ httpGet: vi.fn().mockResolvedValue({}) }))

describe('stockApi', () => {
  it('shouldCallSummaryEndpoint', async () => {
    await getStockSummary()

    expect(httpGet).toHaveBeenCalledWith('/api/stock/summary')
  })

  it('shouldCallStatusEndpoint', async () => {
    await getStockStatus()

    expect(httpGet).toHaveBeenCalledWith('/api/stock/status')
  })
})
