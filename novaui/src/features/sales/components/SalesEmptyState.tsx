import { Receipt, SearchX } from 'lucide-react'
import { Button } from '../../../components/ui/button'
import { EmptyState } from '../../../components/ui/empty-state'

export const EMPTY_SALES_TITLE = 'Aucune vente'

interface SalesEmptyStateProps {
  filtered: boolean
  search: string | undefined
  onShowAll: () => void
  onCreate: () => void
}

export const SalesEmptyState = ({ filtered, search, onShowAll, onCreate }: SalesEmptyStateProps) => {
  if (filtered) {
    return (
      <EmptyState
        icon={SearchX}
        title={EMPTY_SALES_TITLE}
        description="Aucune vente ne correspond à ce filtre."
        actions={<Button variant="secondary" onClick={onShowAll}>Voir toutes les ventes</Button>}
      />
    )
  }
  if (search) {
    return <EmptyState icon={SearchX} title={EMPTY_SALES_TITLE} description={`Aucune vente ne correspond à « ${search} ».`} />
  }
  return (
    <EmptyState
      icon={Receipt}
      title={EMPTY_SALES_TITLE}
      description="Les ventes enregistrées apparaîtront ici."
      actions={<Button onClick={onCreate}>Nouvelle vente</Button>}
    />
  )
}
