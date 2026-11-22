const quantityFormatter = new Intl.NumberFormat('fr-FR', { maximumFractionDigits: 0 })

const PIECE_UNITS = new Set(['', 'u', 'u.', 'unite', 'unité', 'unites', 'unités'])

const isAbbreviation = (word: string): boolean => word.length <= 2 || word.endsWith('.') || /\d/.test(word)

const pluralizeWord = (word: string): string => {
  if (/[sxz]$/i.test(word)) return word
  if (/(au|eu)$/i.test(word)) return `${word}x`
  return `${word}s`
}

const pluralizeUnit = (unit: string): string => {
  const [head, ...rest] = unit.split(' ')
  return [pluralizeWord(head), ...rest].join(' ')
}

const unitLabel = (unit: string, quantity: number): string => {
  const trimmed = unit.trim()
  if (PIECE_UNITS.has(trimmed.toLowerCase())) return 'u.'
  if (isAbbreviation(trimmed.split(' ')[0]) || Math.abs(quantity) < 2) return trimmed
  return pluralizeUnit(trimmed)
}

export const formatStockQuantity = (quantity: number, unit: string): string =>
  `${quantityFormatter.format(quantity)} ${unitLabel(unit, quantity)}`
