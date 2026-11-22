import { FilterBar } from '../../../components/module/FilterBar'
import { Combobox } from '../../../components/ui/combobox'
import { FilterChip } from '../../../components/ui/filter-chip'
import type { Role } from '../../../types/auth'
import { ROLE_OPTIONS, roleOption } from '../constants/roles'
import { DEFAULT_USERS_FILTER, USERS_FILTERS } from '../services/usersFilters'
import type { UserCounts, UsersFilterKey } from '../types/user'

export const USERS_SORT_HINT = 'Les plus récents d’abord'

interface UsersFilterBarProps {
  active: UsersFilterKey
  role: Role | undefined
  counts: UserCounts | null
  onSelect: (filter: UsersFilterKey) => void
  onRole: (role: Role | undefined) => void
}

export const UsersFilterBar = ({ active, role, counts, onSelect, onRole }: UsersFilterBarProps) => (
  <div className="flex flex-col gap-3 md:flex-row md:items-center md:justify-between">
    <FilterBar label="Filtrer les utilisateurs">
      {USERS_FILTERS.map((filter) => (
        <FilterChip
          key={filter.key}
          label={filter.label}
          count={counts?.[filter.countKey]}
          active={filter.key === active}
          onClick={() => onSelect(filter.key === active ? DEFAULT_USERS_FILTER : filter.key)}
        />
      ))}
    </FilterBar>
    <Combobox
      label="Filtrer par rôle"
      hideLabel
      className="md:w-[240px]"
      options={ROLE_OPTIONS}
      emptyOptionLabel="Tous les rôles"
      placeholder="Tous les rôles"
      value={roleOption(role)}
      onChange={(option) => onRole(option ? (option.value as Role) : undefined)}
    />
  </div>
)
