import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { RouterProvider, createMemoryHistory } from '@tanstack/react-router'
import { render, screen } from '@testing-library/react'
import { useModuleDefinitions } from '../../../features/modules/hooks/useModuleDefinitions'
import { createAppRouter } from '../../../navigation/appRouter'
import { readSession } from '../../../services/sessionStore'
import { mockViewport } from '../../../test/mockViewport'

vi.mock('../../../services/sessionStore', () => ({ readSession: vi.fn(), clearSession: vi.fn() }))
vi.mock('../../../features/modules/hooks/useModuleDefinitions', () => ({ useModuleDefinitions: vi.fn() }))

const renderAppAt = async (path: string) => {
  const router = createAppRouter(createMemoryHistory({ initialEntries: [path] }))
  await router.load()
  render(
    <QueryClientProvider client={new QueryClient()}>
      <RouterProvider router={router} />
    </QueryClientProvider>,
  )
}

describe('AppShell', () => {
  beforeEach(() => {
    vi.mocked(readSession).mockReturnValue({ token: 't', email: 'hery.rabe@boutique-tana.mg', role: 'ADMIN' })
    vi.mocked(useModuleDefinitions).mockReturnValue({ modules: [], loading: false, unavailable: true })
  })
  afterEach(() => vi.unstubAllGlobals())

  it('shouldRenderHeaderNavScreenAndDock_onDesktop', async () => {
    mockViewport(1440)
    await renderAppAt('/stock')

    expect(await screen.findByRole('heading', { level: 1, name: 'Stock' })).toBeInTheDocument()
    expect(screen.getByRole('link', { name: 'NOVA, accueil' })).toHaveAttribute('href', '/')
    expect(screen.getByRole('navigation', { name: 'Navigation principale' })).toBeInTheDocument()
    expect(screen.getByRole('button', { name: 'Mon compte, Hery Rabe' })).toBeInTheDocument()
    expect(screen.getByRole('textbox', { name: 'Demander à Eclipse' })).toBeInTheDocument()
    expect(screen.queryByRole('button', { name: 'Ouvrir Eclipse' })).not.toBeInTheDocument()
  })

  it('shouldSwapTopNavForBottomBar_onMobile', async () => {
    mockViewport(375)
    await renderAppAt('/stock')

    expect(await screen.findByRole('heading', { level: 1, name: 'Stock' })).toBeInTheDocument()
    expect(screen.getAllByRole('navigation', { name: 'Navigation principale' })).toHaveLength(1)
    expect(screen.getByRole('button', { name: 'Ouvrir Eclipse' })).toBeInTheDocument()
    expect(screen.getByRole('button', { name: 'Mon compte, Hery Rabe' })).toBeInTheDocument()
    expect(screen.queryByRole('textbox', { name: 'Demander à Eclipse' })).not.toBeInTheDocument()
  })

  it('shouldOfferSkipLink_toMainContent', async () => {
    mockViewport(1440)
    await renderAppAt('/profil')

    expect(await screen.findByRole('link', { name: 'Aller au contenu' })).toHaveAttribute('href', '#contenu')
    expect(screen.queryByRole('textbox', { name: 'Demander à Eclipse' })).not.toBeInTheDocument()
  })
})
