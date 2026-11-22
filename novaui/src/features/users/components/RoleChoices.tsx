import { ChoiceChip } from '../../../components/ui/choice-chip'
import { ROLE_LABELS } from '../../../constants/roleLabels'
import { ROLE_STYLES } from '../../../constants/roleStyles'
import type { Role } from '../../../types/auth'
import { ROLES, ROLES_HINT, toggleRole } from '../constants/roles'

interface RoleChoicesProps {
  value: readonly Role[]
  error?: string
  onChange: (roles: Role[]) => void
}

export const RoleChoices = ({ value, error, onChange }: RoleChoicesProps) => (
  <div className="flex flex-col gap-2">
    <span id="role-choices-label" className="text-label text-ink-primary">Rôles</span>
    <div
      role="group"
      aria-labelledby="role-choices-label"
      aria-invalid={error ? true : undefined}
      tabIndex={error ? -1 : undefined}
      className="flex flex-wrap gap-2 outline-none"
    >
      {ROLES.map((role) => (
        <ChoiceChip
          key={role}
          label={ROLE_LABELS[role]}
          icon={ROLE_STYLES[role].icon}
          selected={value.includes(role)}
          onClick={() => onChange(toggleRole(value, role))}
        />
      ))}
    </div>
    <p className={error ? 'text-micro text-status-critical-fg' : 'text-micro text-ink-tertiary'}>{error ?? ROLES_HINT}</p>
  </div>
)
