import { screen, within } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { useModuleDefinitions } from '../../../features/modules/hooks/useModuleDefinitions'
import { renderWithRouter } from '../../../test/renderWithRouter'
import type { Role } from '../../../types/auth'
import { BottomBar } from '../BottomBar'
import { EclipseDockProvider } from '../EclipseDockProvider'
import { useEclipseDock } from '../eclipse-dock-context'

vi.mock('../../../features/modules/hooks/useModuleDefinitions', () => ({ useModuleDefinitions: vi.fn() }))

const DockState = () => <p>{useEclipseDock().mobileOpen ? 'barre ouverte' : 'barre fermée'}</p>

const renderBar = (role: Role) => {
  vi.mocked(useModuleDefinitions).mockReturnValue({
    modules: [{ id: 7, slug: 'livraisons', name: 'Livraisons', icon: 'truck', createdAt: '2026-11-20T08:00:00Z' }],
    loading: false,
    unavailable: false,
  })
  return renderWithRouter(
    () => (
      <EclipseDockProvider>
        <BottomBar roles={[role]} />
        <DockState />
      </EclipseDockProvider>
    ),
    '/',
  )
}

describe('BottomBar', () => {
  it('shouldShowHomeTwoModulesEclipseAndMore_toAdmin', async () => {
    await renderBar('ADMIN')

    const nav = await screen.findByRole('navigation', { name: 'Navigation principale' })
    expect(within(nav).getAllByRole('link').map((link) => link.textContent)).toEqual(['Accueil', 'Ventes', 'Stock'])
    expect(within(nav).getByRole('link', { name: 'Accueil' })).toHaveAttribute('aria-current', 'page')
    expect(within(nav).getByRole('button', { name: 'Plus' })).toBeInTheDocument()
  })

  it('shouldOpenTheEclipseBar_fromTheCentralButton', async () => {
    await renderBar('VENTE')

    await userEvent.click(await screen.findByRole('button', { name: 'Ouvrir Eclipse' }))

    expect(screen.getByText('barre ouverte')).toBeInTheDocument()
  })

  it('shouldListOtherScreensProfileAndLogout_inTheMoreSheet', async () => {
    await renderBar('ADMIN')

    await userEvent.click(await screen.findByRole('button', { name: 'Plus' }))

    const sheet = screen.getByRole('dialog', { name: 'Plus' })
    expect(within(sheet).getAllByRole('link').map((link) => link.textContent)).toEqual(['Équipe', 'Relations', 'Livraisons'])
    expect(within(sheet).getByRole('button', { name: 'Créer un module' })).toBeInTheDocument()
    expect(within(sheet).getByRole('button', { name: 'Mon profil' })).toBeInTheDocument()
    expect(within(sheet).getByRole('button', { name: 'Se déconnecter' })).toBeInTheDocument()
  })
})
