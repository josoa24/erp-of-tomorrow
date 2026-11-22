import { httpGet, httpPost } from '../../../../services/api/httpClient'
import { createAdvance, decideAdvance, getAdvanceSummary, getAdvances } from '../advancesApi'

vi.mock('../../../../services/api/httpClient', () => ({
  httpGet: vi.fn().mockResolvedValue([]),
  httpPost: vi.fn().mockResolvedValue({}),
}))

describe('advancesApi', () => {
  afterEach(() => vi.clearAllMocks())

  it('shouldFilterByEmployeeAndStatus', async () => {
    await getAdvances({ employeeId: 3, status: 'DRAFT' })

    expect(httpGet).toHaveBeenCalledWith('/api/rh/advances?employeeId=3&status=DRAFT', {})
  })

  it('shouldReadMonthSummary', async () => {
    await getAdvanceSummary()

    expect(httpGet).toHaveBeenCalledWith('/api/rh/advances/summary', {})
  })

  it('shouldPostAdvanceRequest', async () => {
    const request = { employeeId: 3, amount: 80000, reason: 'Rentrée scolaire' }

    await createAdvance(request)

    expect(httpPost).toHaveBeenCalledWith('/api/rh/advances', request)
  })

  it.each([
    ['validate', '/api/rh/advances/5/validate'],
    ['reject', '/api/rh/advances/5/reject'],
  ] as const)('shouldPostDecision_%s', async (decision, path) => {
    await decideAdvance(5, decision)

    expect(httpPost).toHaveBeenCalledWith(path)
  })
})
