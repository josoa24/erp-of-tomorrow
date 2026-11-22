import type { UiAlertItem, UiBlock, UiCell, UiCellKind, UiColumn, UiField, UiKpiItem, UiTone } from '../types/uiBlock'

type Wire = Record<string, unknown>

const TONES: UiTone[] = ['success', 'warning', 'critical', 'info', 'neutral']
const KINDS: UiCellKind[] = ['text', 'number', 'money', 'percent', 'date', 'status']

const asObject = (value: unknown): Wire =>
  typeof value === 'object' && value !== null && !Array.isArray(value) ? (value as Wire) : {}

const asArray = (value: unknown): unknown[] => (Array.isArray(value) ? value : [])

const asString = (value: unknown): string => (typeof value === 'string' ? value : '')

const asTone = (value: unknown): UiTone => TONES.find((tone) => tone === value) ?? 'neutral'

const asKind = (value: unknown): UiCellKind => KINDS.find((kind) => kind === value) ?? 'text'

export const asCell = (value: unknown): UiCell => {
  if (typeof value === 'string' || typeof value === 'number' || typeof value === 'boolean') return value
  const wire = asObject(value)
  return typeof wire.label === 'string' ? { label: wire.label, tone: asTone(wire.tone) } : null
}

const toColumn = (wire: unknown): UiColumn => {
  const value = asObject(wire)
  return { key: asString(value.key), label: asString(value.label), kind: asKind(value.kind) }
}

const toRow = (wire: unknown): Record<string, UiCell> =>
  Object.fromEntries(Object.entries(asObject(wire)).map(([key, cell]) => [key, asCell(cell)]))

const toKpiItem = (wire: unknown): UiKpiItem => {
  const value = asObject(wire)
  return { label: asString(value.label), value: asCell(value.value), kind: asKind(value.kind), hint: asString(value.hint) || undefined }
}

const toField = (wire: unknown): UiField => {
  const value = asObject(wire)
  return { label: asString(value.label), value: asCell(value.value) }
}

const toAlert = (wire: unknown): UiAlertItem => {
  const value = asObject(wire)
  return { title: asString(value.title), detail: asString(value.detail), tone: asTone(value.tone) }
}

const toBlock = (wire: unknown): UiBlock | null => {
  const value = asObject(wire)
  const title = asString(value.title)
  switch (value.type) {
    case 'kpi':
      return { type: 'kpi', title, items: asArray(value.items).map(toKpiItem) }
    case 'table':
      return {
        type: 'table',
        title,
        columns: asArray(value.columns).map(toColumn),
        rows: asArray(value.rows).map(toRow),
        total: typeof value.total === 'number' ? value.total : undefined,
      }
    case 'card':
      return { type: 'card', title, subtitle: asString(value.subtitle), tone: asTone(value.tone), fields: asArray(value.fields).map(toField) }
    case 'alerts':
      return { type: 'alerts', title, items: asArray(value.items).map(toAlert) }
    default:
      return null
  }
}

export const toUiBlocks = (wire: unknown): UiBlock[] => asArray(wire).flatMap((block) => toBlock(block) ?? [])
