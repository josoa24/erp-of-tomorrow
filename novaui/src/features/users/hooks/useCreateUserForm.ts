import { useState } from 'react'
import { EMPTY_USER_FORM, toCreateUserRequest, validateUserForm, type UserFormValues } from '../services/userForms'
import { useCreateUser } from './useUserCommands'
import { useGeneratedPassword } from './useGeneratedPassword'

export const useCreateUserForm = () => {
  const [values, setValues] = useState<UserFormValues>(EMPTY_USER_FORM)
  const [submitted, setSubmitted] = useState(false)
  const [created, setCreated] = useState<{ email: string; password: string } | null>(null)
  const generated = useGeneratedPassword()
  const { mutate, isPending } = useCreateUser()
  const form = { ...values, password: generated.password }
  const errors = submitted ? validateUserForm(form) : {}

  const update = <Key extends keyof UserFormValues>(key: Key, value: UserFormValues[Key]) =>
    setValues((previous) => ({ ...previous, [key]: value }))

  const submit = () => {
    setSubmitted(true)
    if (Object.values(validateUserForm(form)).some(Boolean)) return
    const request = toCreateUserRequest(form)
    mutate(request, { onSuccess: () => setCreated({ email: request.email, password: request.password }) })
  }

  return { values, errors, update, generated, created, submit, saving: isPending }
}
