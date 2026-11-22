import { History } from 'lucide-react'
import { EmptyState } from '../../../components/ui/empty-state'
import { Skeleton } from '../../../components/ui/skeleton'
import { TIMELINE_KIND_ICONS } from '../constants/timelineKinds'
import { formatAriary } from '../services/ariary'
import { formatTimelineDate } from '../services/timelineFormat'
import type { TimelineEntry } from '../types/interaction'

interface TimelineListProps {
  entries: TimelineEntry[]
  loading: boolean
}

export const TimelineList = ({ entries, loading }: TimelineListProps) => {
  if (loading) {
    return (
      <div role="status" aria-label="Chargement de la chronologie" className="flex flex-col gap-3">
        <Skeleton className="h-12" />
        <Skeleton className="h-12" />
        <Skeleton className="h-12" />
      </div>
    )
  }
  if (entries.length === 0) {
    return <EmptyState icon={History} title="Aucun échange pour l’instant" description="Les échanges, ventes et commandes apparaîtront ici." />
  }

  return (
    <ol aria-label="Chronologie" className="flex flex-col">
      {entries.map((entry, index) => {
        const Icon = TIMELINE_KIND_ICONS[entry.kind]
        return (
          <li key={`${entry.kind}-${entry.reference ?? index}-${entry.date}`} className="flex gap-3 border-b border-line-subtle py-3 last:border-b-0">
            <span aria-hidden="true" className="inline-flex size-9 shrink-0 items-center justify-center rounded-pill bg-bg-lilac">
              <Icon className="size-4 text-ink-primary" />
            </span>
            <div className="flex min-w-0 flex-1 flex-col gap-0.5">
              <div className="flex flex-wrap items-baseline justify-between gap-x-3">
                <p className="text-label text-ink-primary">{entry.title}</p>
                <p className="text-micro text-ink-tertiary">{formatTimelineDate(entry.date)}</p>
              </div>
              {entry.detail && <p className="text-body-s break-words text-ink-secondary">{entry.detail}</p>}
              {entry.amount !== null && <p className="text-body-s text-ink-primary">{formatAriary(entry.amount)}</p>}
            </div>
          </li>
        )
      })}
    </ol>
  )
}
