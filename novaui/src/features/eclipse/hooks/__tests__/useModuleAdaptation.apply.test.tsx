import { act, screen } from '@testing-library/react'
import { readSession } from '../../../../services/sessionStore'
import { ApiError } from '../../../../types/apiError'
import { activateAdaptation, fetchAdaptations } from '../../api/adaptationsApi'
import { ADAPTATION_APPLIED_MESSAGE } from '../useApplyAdaptation'
import { ADMIN_SESSION, LOTS_ACTIVATION, renderModuleAdaptation } from './moduleAdaptationHarness'

vi.mock('../../api/adaptationsApi', () => ({ fetchAdaptations: vi.fn(), activateAdaptation: vi.fn() }))
vi.mock('../../../../services/sessionStore', () => ({ readSession: vi.fn(), clearSession: vi.fn() }))

const PREVIEW_PATH = '/stock?eclipse=stock.lots-peremption'

describe('useModuleAdaptation apply', () => {
  beforeEach(() => {
    vi.mocked(readSession).mockReturnValue(ADMIN_SESSION)
    vi.mocked(fetchAdaptations).mockResolvedValue([])
  })
  afterEach(() => vi.clearAllMocks())

  it('shouldActivateRefreshClearAndConfirm_whenApplied', async () => {
    vi.mocked(activateAdaptation).mockResolvedValue(LOTS_ACTIVATION)
    const { router, latest } = await renderModuleAdaptation('stock', PREVIEW_PATH)
    await screen.findByText(/^aperçu : /)
    vi.mocked(fetchAdaptations).mockResolvedValue([LOTS_ACTIVATION])

    await act(async () => latest.current?.apply())

    expect(activateAdaptation).toHaveBeenCalledWith({ moduleKey: 'stock', adaptationKey: 'stock.lots-peremption' })
    expect(await screen.findByText(ADAPTATION_APPLIED_MESSAGE)).toBeInTheDocument()
    expect(screen.getByText('adaptation active')).toBeInTheDocument()
    expect(screen.getByText('aucun aperçu')).toBeInTheDocument()
    expect(router.state.location.search).toEqual({})
  })

  it('shouldReportApplying_untilTheServerAnswers', async () => {
    let answer: (activation: typeof LOTS_ACTIVATION) => void = () => undefined
    vi.mocked(activateAdaptation).mockReturnValue(new Promise((resolve) => (answer = resolve)))
    const { latest } = await renderModuleAdaptation('stock', PREVIEW_PATH)
    await screen.findByText(/^aperçu : /)

    await act(async () => latest.current?.apply())
    await vi.waitFor(() => expect(latest.current?.isApplying).toBe(true))

    await act(async () => answer(LOTS_ACTIVATION))
    await vi.waitFor(() => expect(latest.current?.isApplying).toBe(false))
  })

  it('shouldOpenTheErrorPage_whenActivationFails', async () => {
    vi.mocked(activateAdaptation).mockRejectedValue(new ApiError('forbidden', 403, 'Accès refusé', 'FORBIDDEN'))
    const { router, latest } = await renderModuleAdaptation('stock', PREVIEW_PATH)
    await screen.findByText(/^aperçu : /)

    await act(async () => latest.current?.apply())

    await vi.waitFor(() => expect(router.state.location.pathname).toBe('/erreur'))
    expect(router.state.location.state.error).toMatchObject({ type: 'forbidden', status: 403, from: '/stock' })
  })

  it('shouldDoNothing_whenThereIsNoPreview', async () => {
    const { latest } = await renderModuleAdaptation('stock', '/stock')
    await screen.findByText('aucun aperçu')

    await act(async () => latest.current?.apply())

    expect(activateAdaptation).not.toHaveBeenCalled()
  })
})
