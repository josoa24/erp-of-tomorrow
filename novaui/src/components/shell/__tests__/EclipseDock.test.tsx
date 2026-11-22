import { RouterProvider, createMemoryHistory, createRootRoute, createRoute, createRouter } from '@tanstack/react-router'
import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { proposeAdaptation } from '../../../features/eclipse/api/proposeApi'
import { ApiError } from '../../../types/apiError'
import { AdaptationBar } from '../../module/AdaptationBar'
import { EclipseDock } from '../EclipseDock'
import { EclipseDockProvider } from '../EclipseDockProvider'

vi.mock('../../../features/eclipse/api/proposeApi', () => ({ proposeAdaptation: vi.fn() }))

const renderDockAt = async (path: string, withProposal = false) => {
  const rootRoute = createRootRoute({
    component: () => (
      <EclipseDockProvider>
        <p>Écran prêt</p>
        <EclipseDock />
        {withProposal && (
          <AdaptationBar request="Ajoute le n° de lot" summary="Colonnes ajoutées" canApply onApply={vi.fn()} onCancel={vi.fn()} />
        )}
      </EclipseDockProvider>
    ),
  })
  const stock = createRoute({
    getParentRoute: () => rootRoute,
    path: '/stock',
    staticData: { eclipsePlaceholder: 'Demandez ou adaptez « Stock »…' },
  })
  const profile = createRoute({ getParentRoute: () => rootRoute, path: '/profil', staticData: { hideEclipseDock: true } })
  const other = createRoute({ getParentRoute: () => rootRoute, path: '/modules/livraisons' })
  const router = createRouter({
    routeTree: rootRoute.addChildren([stock, profile, other]),
    history: createMemoryHistory({ initialEntries: [path] }),
  })
  await router.load()
  render(<RouterProvider router={router} />)
}

describe('EclipseDock', () => {
  it('shouldUseTheRoutePlaceholder_onModuleRoutes', async () => {
    await renderDockAt('/stock')

    const input = await screen.findByRole('textbox', { name: 'Demander à Eclipse' })
    expect(input).toHaveAttribute('placeholder', 'Demandez ou adaptez « Stock »…')
    expect(input).toBeEnabled()
  })

  it('shouldAnnounceUpcomingEclipse_whenEclipseIsUnavailable', async () => {
    vi.mocked(proposeAdaptation).mockRejectedValueOnce(new ApiError('network', 503, 'Eclipse indisponible'))
    await renderDockAt('/stock')

    const input = await screen.findByRole('textbox', { name: 'Demander à Eclipse' })
    await userEvent.type(input, 'Ajoute le n° de lot')
    await userEvent.click(screen.getByRole('button', { name: 'Envoyer à Eclipse' }))

    expect(await screen.findByPlaceholderText('Eclipse arrive bientôt')).toBeDisabled()
  })

  it('shouldShowOnCustomModuleRoutesToo', async () => {
    await renderDockAt('/modules/livraisons')

    expect(await screen.findByRole('textbox', { name: 'Demander à Eclipse' })).toBeInTheDocument()
  })

  it('shouldStayHidden_onRoutesThatOptOut', async () => {
    await renderDockAt('/profil')

    await screen.findByText('Écran prêt')
    expect(screen.queryByRole('textbox', { name: 'Demander à Eclipse' })).not.toBeInTheDocument()
  })
})

describe('EclipseDock with an adaptation proposal', () => {
  it('shouldGiveWayToTheAdaptationBar', async () => {
    await renderDockAt('/stock', true)

    expect(await screen.findByRole('region', { name: "Proposition d'Eclipse" })).toBeInTheDocument()
    expect(screen.queryByRole('textbox', { name: 'Demander à Eclipse' })).not.toBeInTheDocument()
  })
})
