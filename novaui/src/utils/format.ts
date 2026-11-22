import { CURRENCY_LABEL } from '../constants/appInfo'

const compactFormatter = new Intl.NumberFormat('fr-FR', {
  notation: 'compact',
  maximumFractionDigits: 2,
})

const decimalFormatter = new Intl.NumberFormat('fr-FR', { maximumFractionDigits: 1 })

export const formatCompactAmount = (value: number): string =>
  `${compactFormatter.format(value)} ${CURRENCY_LABEL}`

export const formatDecimal = (value: number): string => decimalFormatter.format(value)

export const formatSignedPercent = (value: number): string =>
  `${value > 0 ? '+' : ''}${formatDecimal(value)} %`

export const pluralize = (count: number, singular: string, plural: string): string =>
  `${count} ${count > 1 ? plural : singular}`
