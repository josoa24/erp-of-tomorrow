import type { CustomerCategory, NewContactRequest, PartyType } from '../types/contact'

export interface ContactFormValues {
  kind: PartyType
  category: CustomerCategory
  name: string
  phone: string
  email: string
  city: string
  nif: string
  stat: string
  creditLimit: string
}

export type ContactFormErrors = Partial<Record<'name' | 'email' | 'creditLimit', string>>

export const EMPTY_CONTACT_FORM: ContactFormValues = {
  kind: 'CUSTOMER',
  category: 'PARTICULIER',
  name: '',
  phone: '',
  email: '',
  city: '',
  nif: '',
  stat: '',
  creditLimit: '',
}

const EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
const AMOUNT_PATTERN = /^\d{1,12}$/

const optional = (text: string): string | undefined => text.trim() || undefined

const parseAmount = (text: string): number | undefined => {
  const digits = text.replace(/\s/g, '')
  return digits === '' ? undefined : Number(digits)
}

const isValidAmount = (text: string): boolean => {
  const digits = text.replace(/\s/g, '')
  return digits === '' || AMOUNT_PATTERN.test(digits)
}

export const showsFiscalIds = (values: ContactFormValues): boolean =>
  values.kind === 'SUPPLIER' || values.category === 'ENTREPRISE'

export const validateContactForm = (values: ContactFormValues, adapted: boolean): ContactFormErrors => ({
  ...(values.name.trim() === '' && { name: 'Indiquez le nom du contact.' }),
  ...(values.email.trim() !== '' &&
    !EMAIL_PATTERN.test(values.email.trim()) && { email: 'Adresse e-mail invalide, par exemple contact@boutique.mg.' }),
  ...(adapted &&
    values.kind === 'CUSTOMER' &&
    !isValidAmount(values.creditLimit) && { creditLimit: 'Montant en ariary, chiffres seulement, par exemple 2000000.' }),
})

export const hasErrors = (errors: ContactFormErrors): boolean => Object.keys(errors).length > 0

export const toNewContactRequest = (values: ContactFormValues, adapted: boolean): NewContactRequest => {
  const customer = values.kind === 'CUSTOMER'
  const fiscal = adapted && showsFiscalIds(values)
  return {
    kind: values.kind,
    name: values.name.trim(),
    phone: optional(values.phone),
    email: optional(values.email),
    city: optional(values.city),
    ...(customer && { type: values.category }),
    ...(fiscal && { nif: optional(values.nif), stat: optional(values.stat) }),
    ...(adapted && customer && { creditLimit: parseAmount(values.creditLimit) }),
  }
}
