import { DecisionDialog } from '../components/DecisionDialog'
import { useLeaveDecision } from '../hooks/teamMutations'
import { usePendingLeaves } from '../hooks/teamQueries'
import { leaveItem } from '../services/decisionItems'

export const LeaveRequestsPanel = ({ onClose }: { onClose: () => void }) => {
  const { data } = usePendingLeaves(true)
  const { mutate, isPending } = useLeaveDecision()

  return (
    <DecisionDialog
      title="Demandes de congé"
      description="Une décision prise ne peut plus être modifiée."
      emptyText="Aucune demande de congé à valider."
      items={data?.map(leaveItem)}
      deciding={isPending}
      onDecide={(id, decision) => mutate({ id, decision })}
      onClose={onClose}
    />
  )
}
