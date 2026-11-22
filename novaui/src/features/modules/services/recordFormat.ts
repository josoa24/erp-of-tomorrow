import { CURRENCY_LABEL } from '../../../constants/appInfo'
import type { ModuleColumn } from '../types/moduleDefinition'

export const EMPTY_CELL = '—'

const amountFormatter = new Intl.NumberFormat('fr-FR', { maximumFractionDigits: 2 })
const dateFormatter = new Intl.DateTimeFormat('fr-FR', { day: 'numeric', month: 'short', year: 'numeric' })
const dateTimeFormatter = new Intl.DateTimeFormat('fr-FR', {
  day: 'numeric',
  month: 'short',
  hour: '2-digit',
  minute: '2-digit',
})

export const formatMoney = (value: number): string => `${amountFormatter.format(value)} ${CURRENCY_LABEL}`

const referenceLabel = (value: unknown): string => {
  if (typeof value !== 'object' || value === null) return EMPTY_CELL
  const label = (value as { label?: unknown }).label
  return typeof label === 'string' && label !== '' ? label : EMPTY_CELL
}

const toNumber = (value: unknown): number | null => {
  const amount = typeof value === 'string' ? Number(value) : value
  return typeof amount === 'number' && Number.isFinite(amount) ? amount : null
}

const formatTemporal = (value: unknown, formatter: Intl.DateTimeFormat): string => {
  if (typeof value !== 'string') return EMPTY_CELL
  const time = Date.parse(value)
  return Number.isNaN(time) ? value : formatter.format(time)
}

export const formatCell = (column: ModuleColumn, value: unknown): string => {
  if (value === null || value === undefined || value === '') return EMPTY_CELL
  if (column.type === 'REFERENCE') return referenceLabel(value)
  if (column.type === 'MONEY') {
    const amount = toNumber(value)
    return amount === null ? EMPTY_CELL : formatMoney(amount)
  }
  if (column.type === 'DATE') return formatTemporal(value, dateFormatter)
  if (column.type === 'DATETIME') return formatTemporal(value, dateTimeFormatter)
  return String(value)
}
