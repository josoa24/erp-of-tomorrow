import type { IndicatorSpec, ModuleColumn, ModuleDefinitionRequest } from '../types/moduleDefinition'
import type { ModuleProposal } from '../types/moduleRecord'
import { REFERENCE_LABELS } from './referenceOptions'

export interface ProposalSelection {
  name: string
  columns: Record<string, boolean>
  indicators: Record<string, boolean>
}

export const initialSelection = ({ proposal, uncheckedColumns }: ModuleProposal): ProposalSelection => ({
  name: proposal.name,
  columns: Object.fromEntries(proposal.columns.map((column) => [column.key, !uncheckedColumns.includes(column.key)])),
  indicators: Object.fromEntries(proposal.indicators.map((indicator) => [indicator.key, true])),
})

const needs = (indicator: IndicatorSpec): string[] =>
  [indicator.column, indicator.todayColumn, indicator.detailColumn].filter((key): key is string => key !== null)

export const indicatorBlocked = (indicator: IndicatorSpec, selection: ProposalSelection): boolean =>
  needs(indicator).some((key) => selection.columns[key] === false)

export const indicatorChecked = (indicator: IndicatorSpec, selection: ProposalSelection): boolean =>
  selection.indicators[indicator.key] !== false && !indicatorBlocked(indicator, selection)

const FEMININE_TARGETS = new Set(['SALE'])

export const columnLabel = (column: ModuleColumn): string => {
  if (column.reference === null) return column.label
  const linked = FEMININE_TARGETS.has(column.reference) ? 'liée' : 'lié'
  return `${column.label} · ${linked} à ${REFERENCE_LABELS[column.reference]}`
}

export const statusLabel = (proposal: ModuleDefinitionRequest): string =>
  `Statut : ${proposal.statuses.map((status) => status.label).join(' → ')}`

export const toDefinitionRequest = (proposal: ModuleProposal, selection: ProposalSelection): ModuleDefinitionRequest => ({
  ...proposal.proposal,
  name: selection.name.trim(),
  columns: proposal.proposal.columns.filter((column) => selection.columns[column.key] !== false),
  indicators: proposal.proposal.indicators.filter((indicator) => indicatorChecked(indicator, selection)),
})
