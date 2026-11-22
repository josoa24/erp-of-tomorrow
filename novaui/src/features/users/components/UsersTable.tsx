import type { ReactNode } from 'react'
import { DataTable } from '../../../components/module/DataTable'
import { buildUserRowActions, type UserRowHandlers } from '../services/userRowActions'
import { isSameAccount, userDisplayName } from '../services/usersFormat'
import type { ManagedUser } from '../types/user'
import { buildUsersColumns } from './usersColumns'

interface UsersTableProps {
  users: ManagedUser[]
  loading: boolean
  currentEmail: string | undefined
  handlers: UserRowHandlers
  empty: ReactNode
}

export const UsersTable = ({ users, loading, currentEmail, handlers, empty }: UsersTableProps) => (
  <DataTable
    caption="Utilisateurs"
    columns={buildUsersColumns(currentEmail)}
    rows={users}
    getRowKey={(user) => user.id}
    getRowLabel={userDisplayName}
    rowActions={(user) => (isSameAccount(user, currentEmail) ? [] : buildUserRowActions(user, handlers))}
    loading={loading}
    empty={empty}
  />
)
