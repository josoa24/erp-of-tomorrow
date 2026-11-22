import type { IndicatorKind, ModuleDefinitionRequest } from './moduleDefinition'

export interface ReferenceOption {
  id: number
  label: string
}

export interface ModuleRecord {
  id: number
  values: Record<string, unknown>
  status: string
  createdBy: string
  createdAt: string
  updatedAt: string
}

export interface IndicatorValue {
  key: string
  label: string
  icon: string
  kind: IndicatorKind
  value: number
  total: number | null
  money: boolean
  detail: string[]
}

export interface StatusCounts {
  all: number
  byStatus: Record<string, number>
}

export interface ModuleProposal {
  proposal: ModuleDefinitionRequest
  accessText: string
  source: 'GEMINI' | 'TEMPLATE'
  canCreate: boolean
  uncheckedColumns: string[]
  note?: string
  explanation?: string
  suggestions?: string[]
  changes?: string[]
}
