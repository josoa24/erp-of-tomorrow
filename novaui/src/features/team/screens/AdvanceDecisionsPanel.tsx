import { DecisionDialog } from '../components/DecisionDialog'
import { useAdvanceDecision } from '../hooks/teamMutations'
import { useDraftAdvances } from '../hooks/teamQueries'
import { advanceItem } from '../services/decisionItems'
import { fullName } from '../services/teamFormat'
import type { Employee } from '../types/employee'

interface AdvanceDecisionsPanelProps {
  employee: Employee
  onClose: () => void
}

export const AdvanceDecisionsPanel = ({ employee, onClose }: AdvanceDecisionsPanelProps) => {
  const { data } = useDraftAdvances(employee.id)
  const { mutate, isPending } = useAdvanceDecision()

  return (
    <DecisionDialog
      title="Avances à valider"
      description={`Demandes de ${fullName(employee)}. Une décision prise ne peut plus être modifiée.`}
      emptyText="Aucune avance à valider."
      items={data?.map(advanceItem)}
      deciding={isPending}
      onDecide={(id, decision) => mutate({ id, decision })}
      onClose={onClose}
    />
  )
}
