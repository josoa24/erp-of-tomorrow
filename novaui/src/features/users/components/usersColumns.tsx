import { UserCheck, UserX } from 'lucide-react'
import type { DataTableColumn } from '../../../components/module/types'
import { StatusPill } from '../../../components/ui/status-pill'
import { RoleBadges } from '../../../components/module/RoleBadge'
import { formatCreatedAt, formatLastLogin, isSameAccount } from '../services/usersFormat'
import type { ManagedUser } from '../types/user'
import { UserCell } from './UserCell'

export const buildUsersColumns = (currentEmail: string | undefined): DataTableColumn<ManagedUser>[] => [
  { key: 'user', header: 'Utilisateur', render: (user) => <UserCell user={user} own={isSameAccount(user, currentEmail)} /> },
  { key: 'roles', header: 'Rôles', render: (user) => <RoleBadges roles={user.roles} /> },
  {
    key: 'state',
    header: 'État',
    render: (user) =>
      user.enabled ? <StatusPill label="Actif" tone="success" icon={UserCheck} /> : <StatusPill label="Désactivé" tone="neutral" icon={UserX} />,
  },
  { key: 'lastLogin', header: 'Dernière connexion', render: (user) => formatLastLogin(user.lastLoginAt) },
  { key: 'created', header: 'Créé le', render: (user) => formatCreatedAt(user.createdAt) },
]
