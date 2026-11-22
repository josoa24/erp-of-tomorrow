import { CalendarDays, PackageX, Sparkles, type LucideIcon } from 'lucide-react'
import { Button } from '../../../components/ui/button'
import { Count } from '../../../components/ui/count'
import { EmptyState } from '../../../components/ui/empty-state'
import { Panel } from '../../../components/ui/panel'
import type { Decision, DecisionKind } from '../services/homeDecisions'

const ICONS: Record<DecisionKind, LucideIcon> = { order: PackageX, leaves: CalendarDays, suggestion: Sparkles }

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
    <ul className="flex flex-col gap-2">
      {decisions.map((decision) => {
        const Icon = ICONS[decision.kind]
        return (
          <li key={decision.key} className="flex flex-wrap items-center gap-3 rounded-md border border-line-strong px-4 py-3">
            <Icon aria-hidden="true" className="size-5 shrink-0 text-ink-primary" />
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
