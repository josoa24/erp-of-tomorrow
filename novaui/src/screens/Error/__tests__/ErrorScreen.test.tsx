import { RouterProvider, createMemoryHistory, createRootRoute, createRoute, createRouter } from '@tanstack/react-router'
import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import type { ErrorRouteState } from '../../../types/errorRoute'
import { ErrorScreen } from '../ErrorScreen'

const renderAt = async (error?: ErrorRouteState) => {
  const rootRoute = createRootRoute()
  const page = (path: string, component: () => React.JSX.Element) =>
    createRoute({ getParentRoute: () => rootRoute, path, component })
  const router = createRouter({
    routeTree: rootRoute.addChildren([
      page('/erreur', ErrorScreen),
      page('/connexion', () => <p>Page de connexion</p>),
      page('/', () => <p>Accueil</p>),
    ]),
    history: createMemoryHistory({ initialEntries: ['/'] }),
  })
  await router.navigate({ to: '/erreur', state: { error } })
  render(<RouterProvider router={router} />)
}

describe('ErrorScreen', () => {
  it('shouldShowTitleForTypeAndApiMessage', async () => {
    await renderAt({ type: 'forbidden', message: 'Module interdit', from: '/' })

    expect(await screen.findByRole('heading', { name: 'Accès refusé' })).toBeInTheDocument()
    expect(screen.getByText('Module interdit')).toBeInTheDocument()
  })

  it('shouldShowNotFound_whenNoStateIsGiven', async () => {
    await renderAt()

    expect(await screen.findByRole('heading', { name: 'Page introuvable' })).toBeInTheDocument()
  })

  it('shouldGoBackToOrigin_whenRetrying', async () => {
    await renderAt({ type: 'unauthorized', message: 'Refusé', from: '/connexion' })

    await userEvent.click(await screen.findByRole('button', { name: 'Réessayer' }))

    expect(await screen.findByText('Page de connexion')).toBeInTheDocument()
  })

  it('shouldGoHome_whenClickingBack', async () => {
    await renderAt({ type: 'server', message: 'Panne', from: '/connexion' })

    await userEvent.click(await screen.findByRole('button', { name: 'Retour' }))

    expect(await screen.findByText('Accueil')).toBeInTheDocument()
  })
})
