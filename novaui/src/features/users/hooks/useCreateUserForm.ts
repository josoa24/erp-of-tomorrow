import { useState } from 'react'
import { EMPTY_USER_FORM, toCreateUserRequest, validateUserForm, type UserFormValues } from '../services/userForms'
import { useFocusFirstInvalid } from './useFocusFirstInvalid'
import { useCreateUser } from './useUserCommands'
import { useGeneratedPassword } from './useGeneratedPassword'

export const useCreateUserForm = (formId: string) => {
  const [values, setValues] = useState<UserFormValues>(EMPTY_USER_FORM)
  const [submitted, setSubmitted] = useState(false)
  const [attempt, setAttempt] = useState(0)
  const [created, setCreated] = useState<{ email: string; password: string } | null>(null)
  const generated = useGeneratedPassword()
  const { mutate, reset, isPending } = useCreateUser()
  const form = { ...values, password: generated.password }
  const errors = submitted ? validateUserForm(form) : {}
  useFocusFirstInvalid(formId, attempt)

  const update = <Key extends keyof UserFormValues>(key: Key, value: UserFormValues[Key]) =>
    setValues((previous) => ({ ...previous, [key]: value }))

  const submit = () => {
    setSubmitted(true)
    if (Object.values(validateUserForm(form)).some(Boolean)) {
      setAttempt((count) => count + 1)
      return
    }
    const request = toCreateUserRequest(form)
    mutate(request, {
      onSuccess: () => {
        setCreated({ email: request.email, password: request.password })
        reset()
      },
    })
  }

  return { values, errors, update, generated, created, submit, saving: isPending }
}
