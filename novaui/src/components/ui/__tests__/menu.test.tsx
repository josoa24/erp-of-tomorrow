import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { LogOut, User } from 'lucide-react'
import { Menu } from '../menu'
import { MenuItem, MenuSeparator } from '../menu-item'

const renderMenu = (onLogout = vi.fn()) =>
  render(
    <>
      <Menu
        label="Compte"
        trigger={(props) => (
          <button type="button" {...props}>
            Ouvrir le compte
          </button>
        )}
      >
        <MenuItem icon={User}>Mon profil</MenuItem>
        <MenuSeparator />
        <MenuItem icon={LogOut} tone="danger" onSelect={onLogout}>
          Se déconnecter
        </MenuItem>
      </Menu>
      <p>Ailleurs</p>
    </>,
  )

const trigger = () => screen.getByRole('button', { name: 'Ouvrir le compte' })

describe('Menu', () => {
  it('shouldOpenOnClick_andFocusFirstItem', async () => {
    renderMenu()

    await userEvent.click(trigger())

    expect(screen.getByRole('menu', { name: 'Compte' })).toBeInTheDocument()
    expect(trigger()).toHaveAttribute('aria-expanded', 'true')
    expect(screen.getByRole('menuitem', { name: 'Mon profil' })).toHaveFocus()
  })

  it('shouldMoveFocusWithArrows_andWrapAround', async () => {
    renderMenu()
    await userEvent.click(trigger())

    await userEvent.keyboard('{ArrowDown}')
    expect(screen.getByRole('menuitem', { name: 'Se déconnecter' })).toHaveFocus()
    await userEvent.keyboard('{ArrowDown}')
    expect(screen.getByRole('menuitem', { name: 'Mon profil' })).toHaveFocus()
    await userEvent.keyboard('{End}')
    expect(screen.getByRole('menuitem', { name: 'Se déconnecter' })).toHaveFocus()
  })

  it('shouldOpenFromKeyboard_andCloseOnEscapeReturningFocus', async () => {
    renderMenu()
    trigger().focus()

    await userEvent.keyboard('{Enter}')
    expect(screen.getByRole('menu')).toBeInTheDocument()
    await userEvent.keyboard('{Escape}')

    expect(screen.queryByRole('menu')).not.toBeInTheDocument()
    expect(trigger()).toHaveFocus()
  })

  it('shouldRunActionAndClose_whenItemIsSelected', async () => {
    const onLogout = vi.fn()
    renderMenu(onLogout)
    await userEvent.click(trigger())

    const logout = screen.getByRole('menuitem', { name: 'Se déconnecter' })
    expect(logout).toHaveClass('text-action-danger')
    await userEvent.click(logout)

    expect(onLogout).toHaveBeenCalledTimes(1)
    expect(screen.queryByRole('menu')).not.toBeInTheDocument()
  })

  it('shouldClose_whenClickingOutside', async () => {
    renderMenu()
    await userEvent.click(trigger())

    await userEvent.click(screen.getByText('Ailleurs'))

    expect(screen.queryByRole('menu')).not.toBeInTheDocument()
  })
})
