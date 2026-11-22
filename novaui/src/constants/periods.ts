import type { PeriodKey } from '../types/dashboard'

export const PERIOD_OPTIONS: { key: PeriodKey; label: string }[] = [
  { key: 'today', label: "Aujourd'hui" },
  { key: 'week', label: '7 derniers jours' },
  { key: 'month', label: 'Ce mois-ci' },
]
