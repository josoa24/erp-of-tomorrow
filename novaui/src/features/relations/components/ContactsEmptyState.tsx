import { Handshake, SearchX } from 'lucide-react'
import { Button } from '../../../components/ui/button'
import { EmptyState } from '../../../components/ui/empty-state'

interface ContactsEmptyStateProps {
  query: string
  filtered: boolean
  onCreate: () => void
}

export const ContactsEmptyState = ({ query, filtered, onCreate }: ContactsEmptyStateProps) => {
  if (query !== '') {
    return (
      <EmptyState
        icon={SearchX}
        title={`Aucun contact ne correspond à « ${query} »`}
        description="Cherchez par nom, ville, téléphone, e-mail ou NIF."
      />
    )
  }
  if (filtered) {
    return (
      <EmptyState
        icon={Handshake}
        title="Aucun contact dans ce filtre"
        description="Choisissez « Tous » pour revoir l’ensemble de vos contacts."
      />
    )
  }
  return (
    <EmptyState
      icon={Handshake}
      title="Aucun contact pour l’instant"
      description="Ajoutez vos clients et fournisseurs pour suivre vos échanges avec eux."
      actions={<Button onClick={onCreate}>Nouveau contact</Button>}
    />
  )
}
