import { httpGet } from '../httpClient'
import { getHrSummary } from '../rhApi'

vi.mock('../httpClient', () => ({ httpGet: vi.fn().mockResolvedValue({}) }))

describe('rhApi', () => {
  it('shouldCallHrSummaryEndpoint', async () => {
    await getHrSummary()

    expect(httpGet).toHaveBeenCalledWith('/api/rh/summary')
  })
})
