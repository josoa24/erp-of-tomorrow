import { CURRENCY_LABEL } from '../../../constants/appInfo'

const amountFormatter = new Intl.NumberFormat('fr-FR', { maximumFractionDigits: 0 })

export const formatAriary = (value: number): string => `${amountFormatter.format(value)} ${CURRENCY_LABEL}`
