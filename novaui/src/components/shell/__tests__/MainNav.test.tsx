import { screen, within } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { useModuleDefinitions } from '../../../features/modules/hooks/useModuleDefinitions'
import { renderWithRouter } from '../../../test/renderWithRouter'
import type { Role } from '../../../types/auth'
import { MainNav } from '../MainNav'

vi.mock('../../../features/modules/hooks/useModuleDefinitions', () => ({ useModuleDefinitions: vi.fn() }))

const LIVRAISONS = { id: 7, slug: 'livraisons', name: 'Livraisons', icon: 'truck', createdAt: '2026-11-20T08:00:00Z' }

const renderNav = (role: Role, path = '/stock', modules = [LIVRAISONS]) => {
  vi.mocked(useModuleDefinitions).mockReturnValue({ modules, loading: false, unavailable: false })
  return renderWithRouter(() => <MainNav roles={[role]} />, path)
}

const linkNames = () =>
  within(screen.getByRole('navigation', { name: 'Navigation principale' }))
    .getAllByRole('link')
    .map((link) => link.textContent)

describe('MainNav', () => {
  it('shouldShowEveryModuleAndCustomModules_toAdmin', async () => {
    await renderNav('ADMIN')

    expect(await screen.findByRole('link', { name: 'Stock' })).toHaveAttribute('aria-current', 'page')
    expect(linkNames()).toEqual(['Accueil', 'Ventes', 'Stock', 'Équipe', 'Relations', 'Livraisons'])
    expect(screen.getByRole('link', { name: 'Livraisons' })).toHaveAttribute('href', '/modules/livraisons')
  })

  it('shouldShowOnlyHomeSalesAndMyModules_toSalesRole', async () => {
    await renderNav('VENTE', '/ventes', [])

    expect(await screen.findByRole('link', { name: 'Ventes' })).toHaveAttribute('aria-current', 'page')
    expect(linkNames()).toEqual(['Accueil', 'Ventes'])
    expect(within(screen.getByRole('group', { name: 'Mes modules' })).getByRole('button', { name: 'Créer un module' })).toBeInTheDocument()
  })

  it('shouldNotMarkHomeActive_onAModuleRoute', async () => {
    await renderNav('ADMIN', '/stock')

    expect(await screen.findByRole('link', { name: 'Accueil' })).not.toHaveAttribute('aria-current')
  })

  it('shouldRequestModuleCreation_throughTheSearchParam', async () => {
    const { router } = await renderNav('ADMIN', '/stock')

    await userEvent.click(await screen.findByRole('button', { name: 'Créer un module' }))

    expect(router.state.location.pathname).toBe('/stock')
    expect(router.state.location.search).toEqual({ creer: 'module' })
  })
})
