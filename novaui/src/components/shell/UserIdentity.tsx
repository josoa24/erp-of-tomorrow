import { ShieldCheck } from 'lucide-react'
import type { CurrentUser } from '../../types/currentUser'
import { Avatar } from '../ui/avatar'
import { StatusPill } from '../ui/status-pill'

export const UserIdentity = ({ user }: { user: CurrentUser }) => (
  <div role="none" className="flex items-start gap-3 p-3">
    <Avatar name={user.displayName} initials={user.initials} size="m" decorative />
    <div className="flex min-w-0 flex-col items-start gap-1">
      <p className="text-body-strong break-words text-ink-primary">{user.displayName}</p>
      <p className="text-micro break-all text-ink-tertiary">{user.email}</p>
      <StatusPill label={user.roleLabel} icon={ShieldCheck} className="mt-1" />
    </div>
  </div>
)
