import { screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { clearSession, readSession } from '../../../services/sessionStore'
import { renderWithRouter } from '../../../test/renderWithRouter'
import { UserMenu } from '../UserMenu'

vi.mock('../../../services/sessionStore', () => ({ readSession: vi.fn(), clearSession: vi.fn() }))

const avatarButton = () => screen.findByRole('button', { name: 'Mon compte, Hery Rabe' })

describe('UserMenu', () => {
  beforeEach(() => {
    vi.mocked(readSession).mockReturnValue({ token: 't', email: 'hery.rabe@boutique-tana.mg', role: 'ADMIN' })
  })
  afterEach(() => vi.clearAllMocks())

  it('shouldOpenWithKeyboard_andShowIdentity', async () => {
    await renderWithRouter(UserMenu, '/stock')
    ;(await avatarButton()).focus()

    await userEvent.keyboard('{Enter}')

    const menu = screen.getByRole('menu', { name: 'Mon compte' })
    expect(menu).toHaveTextContent('Hery Rabe')
    expect(menu).toHaveTextContent('hery.rabe@boutique-tana.mg')
    expect(menu).toHaveTextContent('Administrateur')
    expect(screen.getByRole('menuitem', { name: 'Mon profil' })).toHaveFocus()
  })

  it('shouldCloseOnEscape_andReturnFocusToAvatar', async () => {
    await renderWithRouter(UserMenu, '/stock')
    await userEvent.click(await avatarButton())

    await userEvent.keyboard('{Escape}')

    expect(screen.queryByRole('menu')).not.toBeInTheDocument()
    expect(await avatarButton()).toHaveFocus()
  })

  it('shouldOpenProfile', async () => {
    const { router } = await renderWithRouter(UserMenu, '/stock')
    await userEvent.click(await avatarButton())

    await userEvent.click(screen.getByRole('menuitem', { name: 'Mon profil' }))

    await waitFor(() => expect(router.state.location.pathname).toBe('/profil'))
  })

  it('shouldLogOut_andGoToLogin', async () => {
    const { router } = await renderWithRouter(UserMenu, '/stock')
    await userEvent.click(await avatarButton())

    await userEvent.click(screen.getByRole('menuitem', { name: 'Se déconnecter' }))

    expect(clearSession).toHaveBeenCalled()
    await waitFor(() => expect(router.state.location.pathname).toBe('/connexion'))
  })
})
