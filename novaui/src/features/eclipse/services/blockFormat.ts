import { CURRENCY_LABEL } from '../../../constants/appInfo'
import type { UiCell, UiCellKind } from '../types/uiBlock'

const EMPTY = '—'
const numberFormat = new Intl.NumberFormat('fr-FR', { maximumFractionDigits: 1 })
const dateFormat = new Intl.DateTimeFormat('fr-FR', { day: 'numeric', month: 'short', year: 'numeric' })

export const isStatusCell = (cell: UiCell): cell is { label: string; tone: 'success' | 'warning' | 'critical' | 'info' | 'neutral' } =>
  typeof cell === 'object' && cell !== null

const formatNumeric = (value: number, kind: UiCellKind): string => {
  if (kind === 'money') return `${numberFormat.format(Math.round(value))} ${CURRENCY_LABEL}`
  if (kind === 'percent') return `${numberFormat.format(value)} %`
  return numberFormat.format(value)
}

const formatDate = (value: string): string => {
  const date = new Date(value)
  return Number.isNaN(date.getTime()) ? value : dateFormat.format(date)
}

export const formatCell = (cell: UiCell, kind: UiCellKind): string => {
  if (cell === null || cell === '') return EMPTY
  if (isStatusCell(cell)) return cell.label
  if (typeof cell === 'number') return formatNumeric(cell, kind)
  if (typeof cell === 'boolean') return cell ? 'Oui' : 'Non'
  return kind === 'date' ? formatDate(cell) : cell
}
