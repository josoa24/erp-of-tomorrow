import { CURRENCY_LABEL } from '../../../constants/appInfo'
import { formatSignedPercent } from '../../../utils/format'
import { percentChange } from '../../../utils/trend'

const amountFormatter = new Intl.NumberFormat('fr-FR', { maximumFractionDigits: 2 })

export const formatAmount = (value: number): string => `${amountFormatter.format(value)} ${CURRENCY_LABEL}`

export const formatChange = (current: number, previous: number, context: string): string | undefined => {
  const change = percentChange(current, previous)
  return change === null ? undefined : `${formatSignedPercent(change)} ${context}`
}
