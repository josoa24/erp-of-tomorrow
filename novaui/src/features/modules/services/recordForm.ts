import type { ModuleColumn } from '../types/moduleDefinition'
import type { ReferenceOption } from '../types/moduleRecord'

export type FieldValue = string | ReferenceOption | null
export type RecordFormValues = Record<string, FieldValue>
export type RecordFormErrors = Record<string, string | undefined>

export const REQUIRED_MESSAGE = 'Champ obligatoire'
export const NUMBER_MESSAGE = 'Saisissez un nombre'

export const emptyRecordForm = (columns: ModuleColumn[]): RecordFormValues =>
  Object.fromEntries(columns.map((column) => [column.key, column.type === 'REFERENCE' ? null : '']))

const parseNumber = (value: string): number | null => {
  const normalized = value.replace(/[\s\u00a0\u202f]/g, '').replace(',', '.')
  if (normalized === '') return null
  const amount = Number(normalized)
  return Number.isFinite(amount) ? amount : null
}

const isEmpty = (value: FieldValue | undefined): boolean => value === null || value === undefined || value === ''

const errorOf = (column: ModuleColumn, value: FieldValue | undefined): string | undefined => {
  if (isEmpty(value)) return column.required ? REQUIRED_MESSAGE : undefined
  const numeric = column.type === 'NUMBER' || column.type === 'MONEY'
  return numeric && typeof value === 'string' && parseNumber(value) === null ? NUMBER_MESSAGE : undefined
}

export const validateRecordForm = (columns: ModuleColumn[], values: RecordFormValues): RecordFormErrors =>
  Object.fromEntries(columns.map((column) => [column.key, errorOf(column, values[column.key])]))

const toRequestValue = (column: ModuleColumn, value: FieldValue): unknown => {
  if (typeof value === 'object' && value !== null) return { id: value.id }
  if (column.type === 'NUMBER' || column.type === 'MONEY') return parseNumber(String(value))
  return String(value).trim()
}

export const toRecordValues = (columns: ModuleColumn[], values: RecordFormValues): Record<string, unknown> =>
  Object.fromEntries(
    columns
      .filter((column) => !isEmpty(values[column.key]))
      .map((column) => [column.key, toRequestValue(column, values[column.key] as FieldValue)]),
  )
