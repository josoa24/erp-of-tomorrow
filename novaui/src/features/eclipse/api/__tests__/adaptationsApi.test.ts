import { httpGet, httpPost } from '../../../../services/api/httpClient'
import { activateAdaptation, fetchAdaptations } from '../adaptationsApi'

vi.mock('../../../../services/api/httpClient', () => ({ httpGet: vi.fn(), httpPost: vi.fn() }))

describe('adaptationsApi', () => {
  afterEach(() => vi.clearAllMocks())

  it('shouldGetActivationsThroughTheGateway', async () => {
    const signal = new AbortController().signal
    vi.mocked(httpGet).mockResolvedValue([])

    await fetchAdaptations({ signal })

    expect(httpGet).toHaveBeenCalledWith('/api/modules/adaptations', { signal })
  })

  it('shouldPostTheModuleAndAdaptationKeys_whenActivating', async () => {
    const activation = { moduleKey: 'stock', adaptationKey: 'stock.lots-peremption' } as const
    vi.mocked(httpPost).mockResolvedValue({ ...activation, activatedBy: 1, activatedAt: '2026-11-22T06:00:00Z' })

    const result = await activateAdaptation(activation)

    expect(httpPost).toHaveBeenCalledWith('/api/modules/adaptations', activation, {})
    expect(result.activatedBy).toBe(1)
  })
})
