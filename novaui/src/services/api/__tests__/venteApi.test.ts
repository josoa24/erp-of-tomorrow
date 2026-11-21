import { httpGet } from '../httpClient'
import { getSalesSummary } from '../venteApi'

vi.mock('../httpClient', () => ({ httpGet: vi.fn().mockResolvedValue({}) }))

describe('venteApi', () => {
  it('shouldPassDateRangeAsQueryParameters', async () => {
    await getSalesSummary({ from: '2026-11-01', to: '2026-11-21' })

    expect(httpGet).toHaveBeenCalledWith('/api/vente/sales/summary?from=2026-11-01&to=2026-11-21')
  })
})
