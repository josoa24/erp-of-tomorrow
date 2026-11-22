import { useCallback, useState } from 'react'

export const useTeamForm = <Values, Request>(
  initial: Values,
  validate: (values: Values) => Partial<Record<keyof Values, string>>,
  toRequest: (values: Values) => Request,
) => {
  const [values, setValues] = useState<Values>(initial)
  const [submitted, setSubmitted] = useState(false)
  const errors: Partial<Record<keyof Values, string>> = submitted ? validate(values) : {}

  const update = useCallback(<Key extends keyof Values>(key: Key, value: Values[Key]) => {
    setValues((previous) => ({ ...previous, [key]: value }))
  }, [])

  const prepare = (): Request | null => {
    setSubmitted(true)
    return Object.values(validate(values)).some(Boolean) ? null : toRequest(values)
  }

  return { values, errors, update, prepare }
}
