import { CalendarDays, ClipboardList, Percent, Users } from 'lucide-react'
import type { KpiItem } from '../../../components/module/types'
import { pluralize } from '../../../utils/format'
import type { AdaptationView } from '../types/adaptationView'
import type { AdvanceSummary } from '../types/advance'
import type { TeamSummary } from '../types/teamSummary'
import { formatAmount } from './teamFormat'

export const PENDING_VALUE = '—'

const advanceKpi = (summary: AdvanceSummary | undefined, highlighted: boolean): KpiItem => ({
  key: 'advances',
  icon: Percent,
  tone: 'mint',
  label: 'Avances du mois',
  value: summary ? formatAmount(summary.monthTotal) : PENDING_VALUE,
  hint: summary && `${pluralize(summary.requests, 'demande', 'demandes')} · ${summary.pending} à valider`,
  highlighted,
})

export const teamKpis = (
  summary: TeamSummary | undefined,
  advances: AdvanceSummary | undefined,
  adaptation: AdaptationView,
  onOpenLeaveRequests: () => void,
): KpiItem[] => {
  const kpis: KpiItem[] = [
    {
      key: 'headcount',
      icon: Users,
      tone: 'lilac',
      label: 'Effectif',
      value: summary?.totalEmployees ?? PENDING_VALUE,
      hint: summary && pluralize(summary.departmentsWithEmployees, 'service', 'services'),
    },
    {
      key: 'onLeave',
      icon: CalendarDays,
      tone: 'blush',
      label: 'En congé',
      value: summary?.onLeaveToday ?? PENDING_VALUE,
      hint: 'aujourd’hui',
    },
    {
      key: 'requests',
      icon: ClipboardList,
      tone: 'mint',
      label: 'Demandes',
      value: summary?.pendingLeaves ?? PENDING_VALUE,
      hint: (
        <button type="button" className="text-micro text-ink-secondary underline underline-offset-2" onClick={onOpenLeaveRequests}>
          congés à valider
        </button>
      ),
    },
  ]
  return adaptation.visible ? [...kpis, advanceKpi(advances, adaptation.highlighted)] : kpis
}

export const teamSubtitle = (summary: TeamSummary | undefined): string | undefined =>
  summary && `${pluralize(summary.totalEmployees, 'personne', 'personnes')} · ${summary.onLeaveToday} en congé aujourd’hui`
