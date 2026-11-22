import { StatusPill } from '../../../components/ui/status-pill'
import { userDisplayName } from '../services/usersFormat'
import type { ManagedUser } from '../types/user'

interface UserCellProps {
  user: ManagedUser
  own: boolean
}

export const UserCell = ({ user, own }: UserCellProps) => (
  <div className="flex min-w-0 flex-col">
    <span className="flex items-center gap-2">
      <span className="truncate text-body-strong text-ink-primary">{userDisplayName(user)}</span>
      {own && <StatusPill label="Vous" tone="info" />}
    </span>
    <span className="truncate text-body-s text-ink-secondary">{user.email}</span>
  </div>
)
