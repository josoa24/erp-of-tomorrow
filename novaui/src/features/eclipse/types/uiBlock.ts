export type UiTone = 'success' | 'warning' | 'critical' | 'info' | 'neutral'
export type UiCellKind = 'text' | 'number' | 'money' | 'percent' | 'date' | 'status'

export interface UiStatusCell {
  label: string
  tone: UiTone
}

export type UiCell = string | number | boolean | null | UiStatusCell

export interface UiColumn {
  key: string
  label: string
  kind: UiCellKind
}

export interface UiKpiItem {
  label: string
  value: UiCell
  kind: UiCellKind
  hint?: string
}

export interface UiField {
  label: string
  value: UiCell
}

export interface UiAlertItem {
  title: string
  detail: string
  tone: UiTone
}

export interface UiChartPoint {
  label: string
  value: number
}

export type UiBlock =
  | { type: 'kpi'; title: string; items: UiKpiItem[] }
  | { type: 'table'; title: string; columns: UiColumn[]; rows: Record<string, UiCell>[]; total?: number }
  | { type: 'card'; title: string; subtitle: string; tone: UiTone; fields: UiField[] }
  | { type: 'alerts'; title: string; items: UiAlertItem[] }
  | { type: 'chart'; title: string; kind: UiCellKind; points: UiChartPoint[] }
