import { PackageOpen, SearchX } from 'lucide-react'
import { Button } from '../../../components/ui/button'
import { EmptyState } from '../../../components/ui/empty-state'

export const EMPTY_STOCK_TITLE = 'Aucun produit'

interface StockEmptyStateProps {
  filtered: boolean
  search: string
  onShowAll: () => void
  onEntry: () => void
}

export const StockEmptyState = ({ filtered, search, onShowAll, onEntry }: StockEmptyStateProps) => {
  if (filtered) {
    return (
      <EmptyState
        icon={SearchX}
        title={EMPTY_STOCK_TITLE}
        description="Aucun produit ne correspond à ce filtre."
        actions={
          <Button variant="secondary" onClick={onShowAll}>
            Voir tous les produits
          </Button>
        }
      />
    )
  }
  if (search) {
    return <EmptyState icon={SearchX} title={EMPTY_STOCK_TITLE} description={`Aucun produit ne correspond à « ${search} ».`} />
  }
  return (
    <EmptyState
      icon={PackageOpen}
      title={EMPTY_STOCK_TITLE}
      description="Les produits du stock et leurs niveaux apparaîtront ici."
      actions={<Button onClick={onEntry}>Entrée de stock</Button>}
    />
  )
}
