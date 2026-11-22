import { act, screen } from '@testing-library/react'
import { readSession } from '../../../../services/sessionStore'
import { fetchAdaptations } from '../../api/adaptationsApi'
import { ADAPTATION_CATALOG } from '../../constants/adaptations'
import { ADMIN_SESSION, LOTS_ACTIVATION, renderModuleAdaptation } from './moduleAdaptationHarness'

vi.mock('../../api/adaptationsApi', () => ({ fetchAdaptations: vi.fn(), activateAdaptation: vi.fn() }))
vi.mock('../../../../services/sessionStore', () => ({ readSession: vi.fn(), clearSession: vi.fn() }))

const LOTS = ADAPTATION_CATALOG['stock.lots-peremption']

describe('useModuleAdaptation', () => {
  beforeEach(() => {
    vi.mocked(readSession).mockReturnValue(ADMIN_SESSION)
    vi.mocked(fetchAdaptations).mockResolvedValue([])
  })
  afterEach(() => vi.clearAllMocks())

  it('shouldBeActive_whenTheModuleHasAnActivation', async () => {
    vi.mocked(fetchAdaptations).mockResolvedValue([LOTS_ACTIVATION])

    await renderModuleAdaptation('stock', '/stock')

    expect(await screen.findByText('adaptation active')).toBeInTheDocument()
  })

  it('shouldStayInactive_forAModuleWithoutActivation', async () => {
    vi.mocked(fetchAdaptations).mockResolvedValue([LOTS_ACTIVATION])

    const { latest } = await renderModuleAdaptation('ventes', '/ventes')

    await vi.waitFor(() => expect(fetchAdaptations).toHaveBeenCalled())
    expect(screen.getByText('adaptation inactive')).toBeInTheDocument()
    expect(latest.current?.active).toBe(false)
  })

  it('shouldExposeThePreview_ofItsOwnModule', async () => {
    await renderModuleAdaptation('stock', '/stock?eclipse=stock.lots-peremption')

    expect(await screen.findByText(`aperçu : ${LOTS.request}`)).toBeInTheDocument()
  })

  it('shouldIgnoreThePreview_ofAnotherModule', async () => {
    const { latest } = await renderModuleAdaptation('ventes', '/ventes?eclipse=stock.lots-peremption')

    expect(await screen.findByText('aucun aperçu')).toBeInTheDocument()
    expect(latest.current?.preview).toBeNull()
  })

  it('shouldDropThePreview_whenTheAdaptationIsAlreadyActive', async () => {
    vi.mocked(fetchAdaptations).mockResolvedValue([LOTS_ACTIVATION])

    await renderModuleAdaptation('stock', '/stock?eclipse=stock.lots-peremption')

    expect(await screen.findByText('adaptation active')).toBeInTheDocument()
    expect(screen.getByText('aucun aperçu')).toBeInTheDocument()
  })

  it('shouldClearThePreviewAndTheUrl_whenCancelled', async () => {
    const { router, latest } = await renderModuleAdaptation('stock', '/stock?eclipse=stock.lots-peremption')
    await screen.findByText(`aperçu : ${LOTS.request}`)

    await act(async () => latest.current?.cancel())

    expect(screen.getByText('aucun aperçu')).toBeInTheDocument()
    expect(router.state.location.search).toEqual({})
  })

  it.each([
    ['ADMIN', true],
    ['STOCK', false],
    ['VENTE', false],
  ] as const)('shouldLetOnlyTheAdministratorApply_%s', async (role, expected) => {
    vi.mocked(readSession).mockReturnValue({ ...ADMIN_SESSION, role })

    const { latest } = await renderModuleAdaptation('stock', '/stock')

    expect(latest.current?.canApply).toBe(expected)
  })
})
