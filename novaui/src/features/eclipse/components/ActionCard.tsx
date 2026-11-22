import { Check, RotateCcw, X } from 'lucide-react'
import type { KeyboardEvent } from 'react'
import { Button } from '../../../components/ui/button'
import { StatusPill } from '../../../components/ui/status-pill'
import { argumentFields, viewOf } from '../services/actionCardView'
import type { ActionEntry } from '../types/agent'

interface ActionCardProps {
  entry: ActionEntry
  onConfirm: () => void
  onCancel: () => void
  onRetry: () => void
}

export const ActionCard = ({ entry, onConfirm, onCancel, onRetry }: ActionCardProps) => {
  const view = viewOf(entry)
  const { status, action, detail } = entry
  const waiting = status === 'confirming' || status === 'cancelling'
  const retryable = status === 'refused' || status === 'expired' || status === 'failed'

  const handleKey = (event: KeyboardEvent<HTMLElement>) => {
    if (status !== 'pending') return
    if (event.key === 'Escape') onCancel()
    if (event.key === 'Enter' && event.target === event.currentTarget) onConfirm()
  }

  return (
    <article
      aria-label={view.eyebrow}
      tabIndex={status === 'pending' ? 0 : undefined}
      onKeyDown={handleKey}
      className={`flex flex-col gap-3 rounded-lg border bg-surface-solid p-4 outline-none focus-visible:shadow-focus-ring ${view.border}`}
    >
      <header className="flex flex-wrap items-center justify-between gap-2">
        <p className="text-caption text-ink-secondary uppercase">{view.eyebrow}</p>
        {view.pill && <StatusPill label={view.pill.label} tone={view.pill.tone} />}
      </header>
      <h4 className="text-body-strong text-ink-primary">{action.summary}</h4>
      <dl className="flex flex-wrap gap-x-6 gap-y-2">
        {argumentFields(action.arguments).map((field) => (
          <div key={field.label} className="flex flex-col">
            <dt className="text-micro text-ink-tertiary">{field.label}</dt>
            <dd className="text-body-s text-ink-primary">{field.value}</dd>
          </div>
        ))}
      </dl>
      {detail && (
        <p role={view.alert ? 'alert' : 'status'} className={`rounded-md px-3 py-2 text-body-s ${view.alert ? 'bg-status-critical-bg text-status-critical-fg' : 'bg-surface-sunken text-ink-secondary'}`}>
          {detail}
        </p>
      )}
      {(status === 'pending' || waiting) && (
        <div className="flex flex-wrap gap-2">
          <Button size="s" leadingIcon={<Check />} loading={status === 'confirming'} disabled={waiting} onClick={onConfirm}>Confirmer</Button>
          <Button size="s" variant="secondary" leadingIcon={<X />} disabled={waiting} onClick={onCancel}>Annuler</Button>
        </div>
      )}
      {retryable && <Button size="s" variant="eclipse" leadingIcon={<RotateCcw />} className="self-start" onClick={onRetry}>Redemander à Eclipse</Button>}
      {view.footer && <p className="text-micro text-ink-tertiary">{view.footer}</p>}
    </article>
  )
}
