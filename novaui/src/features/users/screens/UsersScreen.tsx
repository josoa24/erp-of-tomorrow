import { ListCard } from '../../../components/module/ListCard'
import { ModulePage } from '../../../components/module/ModulePage'
import { Button } from '../../../components/ui/button'
import { useCurrentUser } from '../../../hooks/useCurrentUser'
import type { Role } from '../../../types/auth'
import { UsersDialogs } from '../components/UsersDialogs'
import { UsersEmptyState } from '../components/UsersEmptyState'
import { UsersFilterBar } from '../components/UsersFilterBar'
import { UsersPagination } from '../components/UsersPagination'
import { UsersTable } from '../components/UsersTable'
import { useClampedPage } from '../hooks/useClampedPage'
import { useUserRowHandlers } from '../hooks/useUserRowHandlers'
import { useUsersCounts } from '../hooks/useUsersCounts'
import { useUsersDialogs } from '../hooks/useUsersDialogs'
import { useUsersList } from '../hooks/useUsersList'
import { useUsersSearch } from '../hooks/useUsersSearch'
import { resolveUsersFilter } from '../services/usersFilters'
import { buildUsersKpiItems, usersSubtitle } from '../services/usersKpiItems'
import { toUsersListQuery } from '../services/usersSearch'

const NO_ROLES: Role[] = []

export const USERS_SEARCH_PLACEHOLDER = 'Rechercher un nom ou un e-mail…'

export const UsersScreen = () => {
  const { search, resetKey, setFilter, setRoles, setText, setPage, reset } = useUsersSearch()
  const { users, total, loading, settled } = useUsersList(toUsersListQuery(search))
  const outOfRange = useClampedPage(search.page ?? 1, total, settled, setPage)
  const counts = useUsersCounts()
  const dialogs = useUsersDialogs()
  const handlers = useUserRowHandlers(dialogs)
  const filter = resolveUsersFilter(search.filtre)
  const filtered = filter.key !== 'tous' || search.role !== undefined || search.recherche !== undefined
  const currentEmail = useCurrentUser()?.email

  return (
    <ModulePage
      title="Utilisateurs"
      subtitle={usersSubtitle(counts)}
      search={{ placeholder: USERS_SEARCH_PLACEHOLDER, defaultValue: search.recherche ?? '', onSearch: setText, resetKey }}
      primaryAction={<Button onClick={dialogs.openCreate}>Nouvel utilisateur</Button>}
      kpis={buildUsersKpiItems(counts)}
      kpiLabel="Indicateurs des utilisateurs"
    >
      <ListCard
        label="Utilisateurs"
        toolbar={<UsersFilterBar active={filter.key} roles={search.role ?? NO_ROLES} counts={counts} onSelect={setFilter} onRoles={setRoles} />}
      >
        <UsersTable
          users={users}
          loading={loading || outOfRange}
          currentEmail={currentEmail}
          handlers={handlers}
          empty={<UsersEmptyState filtered={filtered} search={search.recherche} onShowAll={reset} onCreate={dialogs.openCreate} />}
        />
        <UsersPagination page={search.page ?? 1} total={total} onPage={setPage} />
      </ListCard>
      <UsersDialogs dialog={dialogs.dialog} onClose={dialogs.close} />
    </ModulePage>
  )
}
