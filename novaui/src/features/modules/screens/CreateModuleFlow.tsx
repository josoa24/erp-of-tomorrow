import { CreateModuleDescribeDialog } from '../components/CreateModuleDescribeDialog'
import { CreateModulePreviewDialog } from '../components/CreateModulePreviewDialog'
import { useCreateModule } from '../hooks/useCreateModule'
import { useCreateModuleRequest } from '../hooks/useCreateModuleRequest'
import { useModuleProposal } from '../hooks/useModuleProposal'
import { useCurrentUser } from '../../../hooks/useCurrentUser'
import { setCreateModuleDraft, useCreateModuleDraft } from '../services/createModuleDraft'
import { toDefinitionRequest } from '../services/proposalSelection'

const FlowDialogs = ({ initialDescription, onClose }: { initialDescription: string; onClose: () => void }) => {
  const isAdmin = useCurrentUser()?.role === 'ADMIN'
  const flow = useModuleProposal(initialDescription)
  const { create, creating } = useCreateModule(onClose)
  const { proposal, selection } = flow

  if (!proposal || !selection) {
    return (
      <CreateModuleDescribeDialog
        description={flow.description}
        preparing={flow.preparing}
        onChange={flow.setDescription}
        onSubmit={flow.prepare}
        onClose={onClose}
      />
    )
  }
  return (
    <CreateModulePreviewDialog
      proposal={proposal}
      selection={selection}
      canCreate={isAdmin && proposal.canCreate}
      creating={creating}
      refining={flow.refining}
      onRefine={flow.refine}
      onName={flow.setName}
      onColumn={flow.toggleColumn}
      onIndicator={flow.toggleIndicator}
      onBack={flow.backToDescription}
      onCreate={() => create(toDefinitionRequest(proposal, selection))}
      onClose={onClose}
    />
  )
}

export const CreateModuleFlow = () => {
  const { requested, close } = useCreateModuleRequest()
  const draft = useCreateModuleDraft()
  if (!requested) return null

  return (
    <FlowDialogs
      initialDescription={draft}
      onClose={() => {
        setCreateModuleDraft('')
        close()
      }}
    />
  )
}
