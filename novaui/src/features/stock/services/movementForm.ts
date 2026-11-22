import type { MovementRequest, MovementType } from '../types/stock'

export interface MovementFormValues {
  productId: number | null
  quantity: string
  reason: string
  lotNumber: string
  expiryDate: string
}

export type MovementFormErrors = Partial<Record<keyof MovementFormValues, string>>

export interface MovementRules {
  type: MovementType
  withLot: boolean
  today: string
  available?: number
}

export const LOT_NUMBER_MAX_LENGTH = 64
export const REASON_MAX_LENGTH = 255

export const emptyMovementForm = (productId: number | null = null): MovementFormValues => ({
  productId,
  quantity: '',
  reason: '',
  lotNumber: '',
  expiryDate: '',
})

const parseQuantity = (raw: string): number | null => {
  const value = Number(raw.trim())
  return raw.trim() !== '' && Number.isInteger(value) && value > 0 ? value : null
}

const quantityError = (values: MovementFormValues, rules: MovementRules): string | undefined => {
  const quantity = parseQuantity(values.quantity)
  if (quantity === null) return 'Saisissez une quantité entière supérieure à 0.'
  if (rules.type === 'OUT' && rules.available !== undefined && quantity > rules.available)
    return 'La quantité dépasse le stock disponible.'
  return undefined
}

const lotErrors = (values: MovementFormValues, rules: MovementRules): MovementFormErrors => {
  const lotNumber = values.lotNumber.trim()
  if (!rules.withLot || (lotNumber === '' && values.expiryDate === '')) return {}
  if (lotNumber === '') return { lotNumber: 'Indiquez le numéro du lot.' }
  if (values.expiryDate === '') return { expiryDate: 'Indiquez la date de péremption du lot.' }
  if (values.expiryDate < rules.today)
    return { expiryDate: 'La date de péremption doit être aujourd’hui ou plus tard.' }
  return {}
}

export const validateMovementForm = (values: MovementFormValues, rules: MovementRules): MovementFormErrors => {
  const errors: MovementFormErrors = { ...lotErrors(values, rules) }
  if (values.productId === null) errors.productId = 'Choisissez un produit.'
  const quantity = quantityError(values, rules)
  if (quantity) errors.quantity = quantity
  return errors
}

export const hasErrors = (errors: MovementFormErrors): boolean => Object.keys(errors).length > 0

const optional = (value: string): string | undefined => value.trim() || undefined

export const toMovementRequest = (values: MovementFormValues, rules: MovementRules): MovementRequest => {
  const lotNumber = rules.withLot ? optional(values.lotNumber) : undefined
  return {
    type: rules.type,
    quantity: Number(values.quantity.trim()),
    reason: optional(values.reason),
    ...(lotNumber && { lotNumber, expiryDate: values.expiryDate }),
  }
}
