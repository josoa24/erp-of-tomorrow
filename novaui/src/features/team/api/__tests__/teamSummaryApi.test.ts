import { httpGet } from '../../../../services/api/httpClient'
import { getTeamSummary } from '../teamSummaryApi'

vi.mock('../../../../services/api/httpClient', () => ({ httpGet: vi.fn().mockResolvedValue({}) }))

describe('teamSummaryApi', () => {
  it('shouldReadSummary', async () => {
    await getTeamSummary()

    expect(httpGet).toHaveBeenCalledWith('/api/rh/summary', {})
  })
})
