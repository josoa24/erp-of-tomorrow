import { useState } from 'react'
import { Button } from '../../../components/ui/button'
import type { Decision } from '../types/decision'
import type { DecisionItem } from '../types/decisionItem'

interface DecisionRowProps {
  item: DecisionItem
  deciding: boolean
  onDecide: (id: number, decision: Decision) => void
}

const LABELS: Record<Decision, string> = { validate: 'Valider', reject: 'Refuser' }

export const DecisionRow = ({ item, deciding, onDecide }: DecisionRowProps) => {
  const [chosen, setChosen] = useState<Decision | null>(null)

  return (
    <li className="flex flex-wrap items-center justify-between gap-3 rounded-md border border-line-strong px-4 py-3">
      <span className="flex min-w-0 flex-col">
        <span className="text-label text-ink-primary">{item.title}</span>
        <span className="text-micro text-ink-tertiary">{item.detail}</span>
      </span>
      {chosen === null ? (
        <span className="flex gap-2">
          <Button size="s" variant="secondary" onClick={() => setChosen('reject')}>Refuser</Button>
          <Button size="s" onClick={() => setChosen('validate')}>Valider</Button>
        </span>
      ) : (
        <span className="flex gap-2">
          <Button size="s" variant="secondary" disabled={deciding} onClick={() => setChosen(null)}>Annuler</Button>
          <Button size="s" loading={deciding} onClick={() => onDecide(item.id, chosen)}>
            {`Confirmer : ${LABELS[chosen].toLowerCase()}`}
          </Button>
        </span>
      )}
    </li>
  )
}
