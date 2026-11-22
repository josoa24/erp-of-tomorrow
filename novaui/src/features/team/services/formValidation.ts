export const REQUIRED_MESSAGE = 'Champ obligatoire'
export const AMOUNT_MESSAGE = 'Saisissez un montant en ariary, par exemple 150 000'

const EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

export const isBlank = (value: string): boolean => value.trim() === ''

export const isEmail = (value: string): boolean => EMAIL_PATTERN.test(value.trim())

export const parseAmount = (value: string): number | null => {
  const normalized = value.replace(/[\s  ]/g, '').replace(',', '.')
  if (normalized === '') return null
  const amount = Number(normalized)
  return Number.isFinite(amount) ? amount : null
}

export const hasErrors = (errors: Record<string, string | undefined>): boolean =>
  Object.values(errors).some(Boolean)

export const optionalText = (value: string): string | undefined => (isBlank(value) ? undefined : value.trim())
