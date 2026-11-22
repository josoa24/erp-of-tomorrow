import { useCallback, useState } from 'react'
import { toBusinessIsoDate } from '../services/businessDay'
import {
  emptyInteractionForm,
  toInteractionRequest,
  validateInteractionForm,
  type InteractionFormErrors,
  type InteractionFormValues,
} from '../services/interactionForm'
import type { InteractionRequest } from '../types/interaction'

const NO_ERROR: InteractionFormErrors = {}

export const useInteractionForm = () => {
  const [today] = useState(() => toBusinessIsoDate(new Date()))
  const [values, setValues] = useState<InteractionFormValues>(() => emptyInteractionForm(today))
  const [submitted, setSubmitted] = useState(false)
  const errors = submitted ? validateInteractionForm(values, today) : NO_ERROR

  const update = useCallback(<Key extends keyof InteractionFormValues>(key: Key, value: InteractionFormValues[Key]) => {
    setValues((previous) => ({ ...previous, [key]: value }))
  }, [])

  const prepare = (): InteractionRequest | null => {
    setSubmitted(true)
    const invalid = Object.keys(validateInteractionForm(values, today)).length > 0
    return invalid ? null : toInteractionRequest(values, today)
  }

  return { values, errors, today, update, prepare }
}
