import type { KpiItem } from '../../../components/module/types'
import { moduleIconFor } from './moduleIcon'
import { formatMoney } from './recordFormat'
import type { IndicatorSpec } from '../types/moduleDefinition'
import type { IndicatorValue } from '../types/moduleRecord'

const PENDING_KPI = '—'
const TONES = ['mint', 'blush', 'lilac'] as const

const listOf = (names: string[]): string =>
  names.length < 2 ? (names[0] ?? '') : `${names.slice(0, -1).join(', ')} et ${names[names.length - 1]}`

const valueOf = (indicator: IndicatorValue): string => {
  if (indicator.kind === 'RATIO_TODAY') return `${indicator.value} / ${indicator.total ?? indicator.value}`
  return indicator.money ? formatMoney(indicator.value) : String(indicator.value)
}

const toKpi = (indicator: IndicatorValue, index: number): KpiItem => ({
  key: indicator.key,
  icon: moduleIconFor(indicator.icon),
  tone: TONES[index % TONES.length],
  label: indicator.label,
  value: valueOf(indicator),
  hint: indicator.detail.length > 0 ? listOf(indicator.detail) : undefined,
})

const placeholder = (spec: IndicatorSpec, index: number): KpiItem => ({
  key: spec.key,
  icon: moduleIconFor(spec.icon),
  tone: TONES[index % TONES.length],
  label: spec.label,
  value: PENDING_KPI,
})

export const moduleKpis = (specs: IndicatorSpec[], values: IndicatorValue[] | undefined): KpiItem[] =>
  values ? values.map(toKpi) : specs.map(placeholder)
