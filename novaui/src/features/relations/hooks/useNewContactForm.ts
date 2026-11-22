import { useCallback, useState } from 'react'
import {
  EMPTY_CONTACT_FORM,
  hasErrors,
  toNewContactRequest,
  validateContactForm,
  type ContactFormErrors,
  type ContactFormValues,
} from '../services/contactForm'
import type { NewContactRequest } from '../types/contact'

const NO_ERROR: ContactFormErrors = {}

export const useNewContactForm = (adapted: boolean) => {
  const [values, setValues] = useState<ContactFormValues>(EMPTY_CONTACT_FORM)
  const [submitted, setSubmitted] = useState(false)
  const errors = submitted ? validateContactForm(values, adapted) : NO_ERROR

  const update = useCallback(<Key extends keyof ContactFormValues>(key: Key, value: ContactFormValues[Key]) => {
    setValues((previous) => ({ ...previous, [key]: value }))
  }, [])

  const prepare = (): NewContactRequest | null => {
    setSubmitted(true)
    return hasErrors(validateContactForm(values, adapted)) ? null : toNewContactRequest(values, adapted)
  }

  return { values, errors, update, prepare }
}
