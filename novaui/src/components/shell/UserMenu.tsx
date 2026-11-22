import { useNavigate } from '@tanstack/react-router'
import { LogOut, User } from 'lucide-react'
import { useCurrentUser } from '../../hooks/useCurrentUser'
import { useLogout } from '../../hooks/useLogout'
import { ROUTES } from '../../navigation/routes'
import { Avatar } from '../ui/avatar'
import { Menu } from '../ui/menu'
import { MenuItem, MenuSeparator } from '../ui/menu-item'
import { UserIdentity } from './UserIdentity'

export const UserMenu = () => {
  const user = useCurrentUser()
  const logout = useLogout()
  const navigate = useNavigate()
  if (!user) return null

  return (
    <Menu
      label="Mon compte"
      className="w-[300px] gap-1 rounded-lg p-2"
      trigger={(props) => (
        <button
          type="button"
          aria-label={`Mon compte, ${user.displayName}`}
          className="rounded-pill outline-none focus-visible:shadow-focus-ring"
          {...props}
        >
          <Avatar name={user.displayName} initials={user.initials} size="l" decorative />
        </button>
      )}
    >
      <UserIdentity user={user} />
      <MenuSeparator />
      <MenuItem icon={User} onSelect={() => void navigate({ to: ROUTES.profile })}>
        Mon profil
      </MenuItem>
      <MenuSeparator />
      <MenuItem icon={LogOut} tone="danger" onSelect={logout}>
        Se déconnecter
      </MenuItem>
    </Menu>
  )
}
