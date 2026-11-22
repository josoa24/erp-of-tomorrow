import { ApiError } from '../../../../types/apiError'
import { fetchAdaptations } from '../../api/adaptationsApi'
import { loadActiveAdaptations } from '../activeAdaptations'

vi.mock('../../api/adaptationsApi', () => ({ fetchAdaptations: vi.fn() }))

const LOTS = {
  moduleKey: 'stock',
  adaptationKey: 'stock.lots-peremption',
  activatedBy: 1,
  activatedAt: '2026-11-22T06:00:00Z',
} as const

describe('loadActiveAdaptations', () => {
  it('shouldReturnActivations_whenTheModuleServiceAnswers', async () => {
    vi.mocked(fetchAdaptations).mockResolvedValue([LOTS])

    expect(await loadActiveAdaptations()).toEqual([LOTS])
  })

  it.each([404, 503])('shouldReturnNone_whenTheModuleServiceAnswers_%s', async (status) => {
    vi.mocked(fetchAdaptations).mockRejectedValue(new ApiError('network', status, 'Indisponible'))

    expect(await loadActiveAdaptations()).toEqual([])
  })

  it('shouldRethrow_whenTheErrorIsNotAMissingService', async () => {
    const failure = new ApiError('server', 500, 'Erreur serveur')
    vi.mocked(fetchAdaptations).mockRejectedValue(failure)

    await expect(loadActiveAdaptations()).rejects.toBe(failure)
  })
})
