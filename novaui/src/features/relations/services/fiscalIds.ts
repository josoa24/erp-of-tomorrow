const NIF_GROUPS = [4, 3, 3]
const STAT_GROUPS = [5, 2, 4, 1, 5]

const groupDigits = (value: string, groups: number[]): string => {
  const total = groups.reduce((sum, size) => sum + size, 0)
  if (!new RegExp(`^\\d{${total}}$`).test(value)) return value
  let start = 0
  return groups
    .map((size) => {
      const part = value.slice(start, start + size)
      start += size
      return part
    })
    .join(' ')
}

const clean = (value: string | null): string | null => {
  const trimmed = value?.trim() ?? ''
  return trimmed === '' ? null : trimmed.replace(/\s+/g, '')
}

export const formatNif = (nif: string | null): string | null => {
  const value = clean(nif)
  return value && groupDigits(value, NIF_GROUPS)
}

export const formatStat = (stat: string | null): string | null => {
  const value = clean(stat)
  return value && groupDigits(value, STAT_GROUPS)
}
