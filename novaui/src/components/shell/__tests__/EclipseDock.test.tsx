import { RouterProvider, createMemoryHistory, createRootRoute, createRoute, createRouter } from '@tanstack/react-router'
import { render, screen } from '@testing-library/react'
import { AdaptationBar } from '../../module/AdaptationBar'
import { EclipseDock } from '../EclipseDock'
import { EclipseDockProvider } from '../EclipseDockProvider'

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
  it('shouldAnnounceUpcomingEclipse_onModuleRoutes', async () => {
    await renderDockAt('/stock')

    const input = await screen.findByRole('textbox', { name: 'Demander à Eclipse' })
    expect(input).toHaveAttribute('placeholder', 'Eclipse arrive bientôt')
    expect(input).toBeDisabled()
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
