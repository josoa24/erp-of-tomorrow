import { FilterBar } from '../../../components/module/FilterBar'
import { FilterChip } from '../../../components/ui/filter-chip'
import { ROLE_LABELS } from '../../../constants/roleLabels'
import type { Role } from '../../../types/auth'
import { ROLES } from '../constants/roles'
import { DEFAULT_USERS_FILTER, USERS_FILTERS } from '../services/usersFilters'
import type { UserCounts, UsersFilterKey } from '../types/user'

export const USERS_SORT_HINT = 'Les plus récents d’abord'

interface UsersFilterBarProps {
  active: UsersFilterKey
  roles: readonly Role[]
  counts: UserCounts | null
  onSelect: (filter: UsersFilterKey) => void
  onRoles: (roles: Role[]) => void
}

const toggleRole = (roles: readonly Role[], role: Role): Role[] =>
  roles.includes(role) ? roles.filter((item) => item !== role) : [...roles, role]

export const UsersFilterBar = ({ active, roles, counts, onSelect, onRoles }: UsersFilterBarProps) => (
  <FilterBar label="Filtrer les utilisateurs" sortHint={USERS_SORT_HINT}>
    {USERS_FILTERS.map((filter) => (
      <FilterChip
        key={filter.key}
        label={filter.label}
        count={counts?.[filter.countKey]}
        active={filter.key === active}
        onClick={() => onSelect(filter.key === active ? DEFAULT_USERS_FILTER : filter.key)}
      />
    ))}
    <span aria-hidden="true" className="my-1 w-px shrink-0 bg-line-strong" />
    {ROLES.map((role) => (
      <FilterChip key={role} label={ROLE_LABELS[role]} active={roles.includes(role)} onClick={() => onRoles(toggleRole(roles, role))} />
    ))}
  </FilterBar>
)
