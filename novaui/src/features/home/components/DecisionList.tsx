import { CalendarDays, PackageX, Sparkles, type LucideIcon } from 'lucide-react'
import { Button } from '../../../components/ui/button'
import { Count } from '../../../components/ui/count'
import { EmptyState } from '../../../components/ui/empty-state'
import { Panel } from '../../../components/ui/panel'
import type { Decision, DecisionKind } from '../services/homeDecisions'

const ICONS: Record<DecisionKind, LucideIcon> = { order: PackageX, leaves: CalendarDays, suggestion: Sparkles }

const ICON_TONES: Record<DecisionKind, string> = {
  order: 'bg-status-critical-bg text-status-critical-fg',
  leaves: 'bg-bg-lilac text-ink-primary',
  suggestion: 'bg-bg-lilac text-ink-primary',
}

interface DecisionListProps {
  decisions: Decision[]
  onAct: (decision: Decision) => void
}

export const DecisionList = ({ decisions, onAct }: DecisionListProps) => (
  <Panel aria-labelledby="a-decider" className="flex flex-col gap-3">
    <div className="flex items-center gap-2">
      <h2 id="a-decider" className="text-heading text-ink-primary">À décider</h2>
      <Count value={decisions.length} tone="night" />
    </div>
    {decisions.length === 0 && <EmptyState icon={Sparkles} title="Tout est traité" />}
    <ul className="flex flex-col divide-y divide-line-subtle">
      {decisions.map((decision) => {
        const Icon = ICONS[decision.kind]
        return (
          <li key={decision.key} className="flex flex-wrap items-center gap-3 py-3">
            <span className={`inline-flex size-9 shrink-0 items-center justify-center rounded-pill ${ICON_TONES[decision.kind]}`}>
              <Icon aria-hidden="true" className="size-5" />
            </span>
            <span className="flex min-w-0 flex-1 flex-col">
              <span className="text-label text-ink-primary">{decision.title}</span>
              <span className="text-micro text-ink-tertiary">{decision.detail}</span>
            </span>
            <Button size="s" variant="secondary" onClick={() => onAct(decision)}>{decision.action}</Button>
          </li>
        )
      })}
    </ul>
  </Panel>
)
