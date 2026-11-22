import { useCreateContact } from './useCreateContact'
import { useNewContactForm } from './useNewContactForm'

export const useNewContact = (adapted: boolean, onDone: () => void) => {
  const form = useNewContactForm(adapted)
  const { create, creating } = useCreateContact(onDone)

  const submit = () => {
    const request = form.prepare()
    if (request) create(request)
  }

  return { ...form, submit, creating }
}
