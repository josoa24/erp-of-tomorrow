import { screen } from '@testing-library/react'
import { readSession } from '../../../../services/sessionStore'
import { ApiError } from '../../../../types/apiError'
import { fetchAdaptations } from '../../api/adaptationsApi'
import { ADMIN_SESSION, renderModuleAdaptation } from './moduleAdaptationHarness'

vi.mock('../../api/adaptationsApi', () => ({ fetchAdaptations: vi.fn(), activateAdaptation: vi.fn() }))
vi.mock('../../../../services/sessionStore', () => ({ readSession: vi.fn(), clearSession: vi.fn() }))

describe('useActiveAdaptations', () => {
  beforeEach(() => vi.mocked(readSession).mockReturnValue(ADMIN_SESSION))
  afterEach(() => vi.clearAllMocks())

  it.each([404, 503])('shouldStayOnTheScreenWithoutActivation_whenModuleServiceAnswers_%s', async (status) => {
    vi.mocked(fetchAdaptations).mockRejectedValue(new ApiError('network', status, 'Indisponible'))

    const { router } = await renderModuleAdaptation('stock', '/stock')

    await vi.waitFor(() => expect(fetchAdaptations).toHaveBeenCalled())
    expect(await screen.findByText('adaptation inactive')).toBeInTheDocument()
    expect(router.state.location.pathname).toBe('/stock')
  })

  it('shouldOpenTheErrorPage_whenReadingActivationsFailsOtherwise', async () => {
    vi.mocked(fetchAdaptations).mockRejectedValue(new ApiError('server', 500, 'Erreur serveur', 'SERVER'))

    const { router } = await renderModuleAdaptation('stock', '/stock')

    await vi.waitFor(() => expect(router.state.location.pathname).toBe('/erreur'))
    expect(router.state.location.state.error).toMatchObject({ type: 'server', status: 500, from: '/stock' })
  })
})
