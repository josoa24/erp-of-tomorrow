import { Sparkles } from 'lucide-react'
import { Button } from '../ui/button'
import { useHideEclipseDock } from '../shell/eclipse-dock-context'

export const APPLY_RESTRICTED_LABEL = 'Seul un administrateur peut appliquer'

export interface AdaptationBarProps {
  request: string
  summary: string
  canApply: boolean
  applying?: boolean
  onApply: () => void
  onCancel: () => void
}

export const AdaptationBar = ({ request, summary, canApply, applying = false, onApply, onCancel }: AdaptationBarProps) => {
  useHideEclipseDock()

  return (
    <section
      aria-label="Proposition d'Eclipse"
      className="fixed inset-x-4 bottom-[calc(env(safe-area-inset-bottom)+16px)] z-30 mx-auto flex max-w-[1017px] flex-col gap-3 rounded-xl bg-surface-night py-3 pr-3 pl-4 shadow-elevation-2 md:bottom-7 md:flex-row md:items-center md:gap-4 md:rounded-pill"
    >
      <div className="flex min-w-0 flex-1 items-center gap-4">
        <span aria-hidden="true" className="inline-flex size-10 shrink-0 items-center justify-center rounded-pill bg-surface-solid text-brand-violet">
          <Sparkles className="size-5" />
        </span>
        <div aria-live="polite" className="flex min-w-0 flex-col">
          <p className="text-body-strong text-ink-inverse">« {request} »</p>
          <p className="text-micro text-brand-lilac">{summary}</p>
        </div>
      </div>
      <div className="flex shrink-0 flex-wrap items-center justify-end gap-3">
        <Button variant="secondary" size="s" onClick={onCancel} disabled={applying}>
          Annuler
        </Button>
        {canApply ? (
          <Button variant="eclipse" loading={applying} onClick={onApply}>
            Appliquer pour l’équipe
          </Button>
        ) : (
          <p className="text-label text-brand-lilac">{APPLY_RESTRICTED_LABEL}</p>
        )}
      </div>
    </section>
  )
}
