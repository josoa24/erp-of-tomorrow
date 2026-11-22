import { SearchX, Users } from 'lucide-react'
import { Button } from '../../../components/ui/button'
import { EmptyState } from '../../../components/ui/empty-state'

export const EMPTY_USERS_TITLE = 'Aucun utilisateur'

interface UsersEmptyStateProps {
  filtered: boolean
  search: string | undefined
  onShowAll: () => void
  onCreate: () => void
}

export const UsersEmptyState = ({ filtered, search, onShowAll, onCreate }: UsersEmptyStateProps) => {
  if (filtered) {
    return (
      <EmptyState
        icon={SearchX}
        title={EMPTY_USERS_TITLE}
        description={search ? `Aucun utilisateur ne correspond à « ${search} ».` : 'Aucun utilisateur ne correspond à ces filtres.'}
        actions={<Button variant="secondary" onClick={onShowAll}>Voir tous les utilisateurs</Button>}
      />
    )
  }
  return (
    <EmptyState
      icon={Users}
      title={EMPTY_USERS_TITLE}
      description="Les comptes créés apparaîtront ici."
      actions={<Button onClick={onCreate}>Nouvel utilisateur</Button>}
    />
  )
}
