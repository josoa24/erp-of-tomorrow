import { useCallback, useState } from 'react'
import type { FormEvent } from 'react'
import {
  emptyMovementForm,
  hasErrors,
  toMovementRequest,
  validateMovementForm,
  type MovementFormErrors,
  type MovementFormValues,
  type MovementRules,
} from '../services/movementForm'
import type { MovementRequest } from '../types/stock'

const NO_ERRORS: MovementFormErrors = {}

export const useMovementForm = (rules: MovementRules, initialProductId: number | null) => {
  const [values, setValues] = useState<MovementFormValues>(() => emptyMovementForm(initialProductId))
  const [submitted, setSubmitted] = useState(false)
  const errors = validateMovementForm(values, rules)

  const setField = useCallback(<Key extends keyof MovementFormValues>(key: Key, value: MovementFormValues[Key]) => {
    setValues((previous) => ({ ...previous, [key]: value }))
  }, [])

  const submit = (onValid: (productId: number, movement: MovementRequest) => void) => (event: FormEvent) => {
    event.preventDefault()
    setSubmitted(true)
    if (hasErrors(errors) || values.productId === null) return
    onValid(values.productId, toMovementRequest(values, rules))
  }

  return { values, setField, errors: submitted ? errors : NO_ERRORS, submit }
}
