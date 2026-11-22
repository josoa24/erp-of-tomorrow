import { ROLE_LABELS } from '../../constants/roleLabels'
import { ROLE_STYLES } from '../../constants/roleStyles'
import type { Role } from '../../types/auth'
import { StatusPill } from '../ui/status-pill'

export const RoleBadge = ({ role }: { role: Role }) => {
  const { icon, tone, className } = ROLE_STYLES[role]
  return <StatusPill label={ROLE_LABELS[role]} tone={tone} icon={icon} className={`max-w-full ${className ?? ''}`.trim()} />
}

export const RoleBadges = ({ roles }: { roles: readonly Role[] }) => (
  <div className="flex flex-wrap gap-1">
    {roles.map((role) => (
      <RoleBadge key={role} role={role} />
    ))}
  </div>
)
