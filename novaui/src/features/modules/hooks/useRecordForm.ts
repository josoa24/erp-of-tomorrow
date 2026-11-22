import { useCallback, useState } from 'react'
import {
  emptyRecordForm,
  toRecordValues,
  validateRecordForm,
  type FieldValue,
  type RecordFormErrors,
  type RecordFormValues,
} from '../services/recordForm'
import type { ModuleColumn } from '../types/moduleDefinition'

const NO_ERROR: RecordFormErrors = {}

export const useRecordForm = (columns: ModuleColumn[]) => {
  const [values, setValues] = useState<RecordFormValues>(() => emptyRecordForm(columns))
  const [submitted, setSubmitted] = useState(false)
  const errors = submitted ? validateRecordForm(columns, values) : NO_ERROR

  const update = useCallback((key: string, value: FieldValue) => {
    setValues((previous) => ({ ...previous, [key]: value }))
  }, [])

  const prepare = (): Record<string, unknown> | null => {
    setSubmitted(true)
    const invalid = Object.values(validateRecordForm(columns, values)).some(Boolean)
    return invalid ? null : toRecordValues(columns, values)
  }

  return { values, errors, update, prepare }
}
