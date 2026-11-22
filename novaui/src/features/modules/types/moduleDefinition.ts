export interface ModuleDefinitionSummary {
  id: number
  slug: string
  name: string
  icon: string
  createdAt: string
}

export type ColumnType = 'TEXT' | 'NUMBER' | 'MONEY' | 'DATE' | 'TIME' | 'DATETIME' | 'CHOICE' | 'REFERENCE'
export type ReferenceTarget = 'CUSTOMER' | 'SALE' | 'EMPLOYEE' | 'PRODUCT' | 'SUPPLIER'
export type StatusTone = 'SUCCESS' | 'WARNING' | 'CRITICAL' | 'INFO' | 'NEUTRAL'
export type IndicatorKind = 'COUNT' | 'SUM' | 'RATIO_TODAY'

export interface ModuleColumn {
  key: string
  label: string
  type: ColumnType
  required: boolean
  listed?: boolean
  choices: string[] | null
  reference: ReferenceTarget | null
}

export interface ModuleStatus {
  key: string
  label: string
  tone: StatusTone
  final: boolean
}

export interface IndicatorSpec {
  key: string
  label: string
  icon: string
  kind: IndicatorKind
  statuses: string[] | null
  column: string | null
  todayColumn: string | null
  detailColumn: string | null
}

export interface ModuleDefinitionRequest {
  name: string
  icon: string
  description?: string | null
  columns: ModuleColumn[]
  statuses: ModuleStatus[]
  indicators: IndicatorSpec[]
}

export interface ModuleDefinition extends ModuleDefinitionRequest {
  id: number
  slug: string
  createdBy: string
  createdAt: string
}
