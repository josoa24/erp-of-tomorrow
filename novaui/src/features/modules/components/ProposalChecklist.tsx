import { Checkbox } from '../../../components/ui/checkbox'
import { columnLabel, indicatorBlocked, indicatorChecked, statusLabel, type ProposalSelection } from '../services/proposalSelection'
import type { ModuleDefinitionRequest } from '../types/moduleDefinition'

interface ProposalChecklistProps {
  proposal: ModuleDefinitionRequest
  selection: ProposalSelection
  onColumn: (key: string, checked: boolean) => void
}

export const ProposalChecklist = ({ proposal, selection, onColumn }: ProposalChecklistProps) => (
  <fieldset className="flex flex-col">
    <legend className="mb-1 text-caption text-ink-secondary uppercase">Colonnes</legend>
    {proposal.columns.map((column) => (
      <Checkbox
        key={column.key}
        label={columnLabel(column)}
        checked={selection.columns[column.key] !== false}
        disabled={column.required}
        onChange={(event) => onColumn(column.key, event.target.checked)}
      />
    ))}
    <Checkbox label={statusLabel(proposal)} checked disabled />
  </fieldset>
)

interface ProposalSideProps {
  proposal: ModuleDefinitionRequest
  selection: ProposalSelection
  accessText: string
  onIndicator: (key: string, checked: boolean) => void
}

export const ProposalSide = ({ proposal, selection, accessText, onIndicator }: ProposalSideProps) => (
  <aside className="flex flex-col gap-3 rounded-xl bg-bg-lilac p-5">
    <fieldset className="flex flex-col">
      <legend className="mb-1 text-caption text-ink-secondary uppercase">Indicateurs</legend>
      {proposal.indicators.map((indicator) => (
        <Checkbox
          key={indicator.key}
          label={indicator.label}
          checked={indicatorChecked(indicator, selection)}
          disabled={indicatorBlocked(indicator, selection)}
          onChange={(event) => onIndicator(indicator.key, event.target.checked)}
        />
      ))}
    </fieldset>
    <div className="flex flex-col gap-1">
      <span className="text-caption text-ink-secondary uppercase">Accès</span>
      <p className="text-body-s text-ink-secondary">{accessText}</p>
    </div>
  </aside>
)
