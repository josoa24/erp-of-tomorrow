import { Check } from 'lucide-react'
import { Button } from '../../../components/ui/button'
import { Dialog } from '../../../components/ui/dialog'
import { Input } from '../../../components/ui/input'
import { useBreakpoint } from '../../../hooks/useBreakpoint'
import type { ProposalSelection } from '../services/proposalSelection'
import type { ModuleProposal } from '../types/moduleRecord'
import { EclipseOrb } from './EclipseOrb'
import { ProposalAssistant } from './ProposalAssistant'
import { ProposalChecklist, ProposalSide } from './ProposalChecklist'

const NAME_MAX = 40
const ADMIN_ONLY = 'Seul un administrateur peut créer un module.'

interface CreateModulePreviewDialogProps {
  proposal: ModuleProposal
  selection: ProposalSelection
  canCreate: boolean
  creating: boolean
  refining: boolean
  onRefine: (instruction: string) => void
  onName: (name: string) => void
  onColumn: (key: string, checked: boolean) => void
  onIndicator: (key: string, checked: boolean) => void
  onBack: () => void
  onCreate: () => void
  onClose: () => void
}

export const CreateModulePreviewDialog = (props: CreateModulePreviewDialogProps) => {
  const { proposal, selection, canCreate, creating, refining, onRefine, onName, onColumn, onIndicator, onBack, onCreate, onClose } = props
  const placement = useBreakpoint() === 'mobile' ? 'sheet' : 'center'
  const nameTooShort = selection.name.trim().length < 2

  return (
    <Dialog
      open
      onClose={onClose}
      size="l"
      placement={placement}
      icon={<EclipseOrb />}
      title={`Eclipse a préparé « ${proposal.proposal.name} »`}
      description="Décochez ce qui ne sert pas. Tout reste modifiable après la création."
      footer={
        <>
          <Button variant="ghost" onClick={onBack} disabled={creating} className="sm:mr-auto">Modifier la demande</Button>
          {!canCreate && <p className="self-center text-body-s text-ink-secondary">{ADMIN_ONLY}</p>}
          <Button variant="secondary" onClick={onClose} disabled={creating}>Annuler</Button>
          {canCreate && (
            <Button leadingIcon={<Check />} loading={creating} disabled={nameTooShort} onClick={onCreate}>Créer le module</Button>
          )}
        </>
      }
    >
      <div className="flex flex-col gap-5">
      <ProposalAssistant
        explanation={proposal.explanation}
        suggestions={proposal.suggestions ?? []}
        changes={proposal.changes ?? []}
        refining={refining}
        onRefine={onRefine}
      />
      {proposal.note && <p className="text-body-s text-ink-secondary">{proposal.note}</p>}
      <div className="grid gap-6 md:grid-cols-[1fr_300px]">
        <div className="flex flex-col gap-5">
          <Input
            label="Nom du module"
            maxLength={NAME_MAX}
            value={selection.name}
            helper="Apparaîtra dans « Mes modules »."
            onChange={(event) => onName(event.target.value)}
          />
          <ProposalChecklist proposal={proposal.proposal} selection={selection} onColumn={onColumn} />
        </div>
        <ProposalSide proposal={proposal.proposal} selection={selection} accessText={proposal.accessText} onIndicator={onIndicator} />
      </div>
      </div>
    </Dialog>
  )
}
