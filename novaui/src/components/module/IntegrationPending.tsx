import { Construction } from 'lucide-react'
import { EmptyState } from '../ui/empty-state'
import { ListCard } from './ListCard'

export const INTEGRATION_PENDING_TITLE = 'Écran en cours d’intégration'

export const IntegrationPending = ({ label }: { label: string }) => (
  <ListCard label={label}>
    <EmptyState
      icon={Construction}
      title={INTEGRATION_PENDING_TITLE}
      description="Cet écran arrive bientôt dans NOVA."
      className="rounded-lg bg-surface-solid"
    />
  </ListCard>
)
