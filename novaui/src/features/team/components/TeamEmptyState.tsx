import { SearchX, Users } from 'lucide-react'
import { Button } from '../../../components/ui/button'
import { EmptyState } from '../../../components/ui/empty-state'

interface TeamEmptyStateProps {
  query: string
  filtered: boolean
  onCreate: () => void
}

export const TeamEmptyState = ({ query, filtered, onCreate }: TeamEmptyStateProps) => {
  if (query !== '') {
    return <EmptyState icon={SearchX} title={`Aucun employé ne correspond à « ${query} »`} description="Cherchez par prénom, nom ou e-mail." />
  }
  if (filtered) {
    return <EmptyState icon={Users} title="Personne dans ce filtre" description="Choisissez « Tous » pour revoir toute l’équipe." />
  }
  return (
    <EmptyState
      icon={Users}
      title="Aucun employé pour l’instant"
      description="Ajoutez les personnes de votre équipe pour suivre les congés et les présences."
      actions={<Button onClick={onCreate}>Ajouter un employé</Button>}
    />
  )
}
