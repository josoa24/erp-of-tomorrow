import { httpGet, httpPost } from '../../../../services/api/httpClient'
import { createLeave, decideLeave, getLeavesByStatus } from '../leavesApi'

vi.mock('../../../../services/api/httpClient', () => ({
  httpGet: vi.fn().mockResolvedValue([]),
  httpPost: vi.fn().mockResolvedValue({}),
}))

describe('leavesApi', () => {
  afterEach(() => vi.clearAllMocks())

  it('shouldFilterByStatus', async () => {
    await getLeavesByStatus('DRAFT')

    expect(httpGet).toHaveBeenCalledWith('/api/rh/leaves?status=DRAFT', {})
  })

  it('shouldPostLeaveRequest', async () => {
    const request = { employeeId: 3, startDate: '2026-12-23', endDate: '2026-12-27', reason: 'Fêtes' }

    await createLeave(request)

    expect(httpPost).toHaveBeenCalledWith('/api/rh/leaves', request)
  })

  it.each([
    ['validate', '/api/rh/leaves/7/validate'],
    ['reject', '/api/rh/leaves/7/reject'],
  ] as const)('shouldPostDecision_%s', async (decision, path) => {
    await decideLeave(7, decision)

    expect(httpPost).toHaveBeenCalledWith(path)
  })
})
