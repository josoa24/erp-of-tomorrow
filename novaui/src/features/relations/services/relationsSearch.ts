export const RELATIONS_FILTERS = ['clients', 'fournisseurs', 'a-relancer', 'professionnels'] as const
export type RelationsFilter = (typeof RELATIONS_FILTERS)[number]

export interface RelationsSearch {
  filtre?: RelationsFilter
  q?: string
}

const isRelationsFilter = (value: unknown): value is RelationsFilter =>
  typeof value === 'string' && (RELATIONS_FILTERS as readonly string[]).includes(value)

const toSearchText = (value: unknown): string | undefined => {
  if (typeof value !== 'string' && typeof value !== 'number') return undefined
  const text = String(value).trim()
  return text === '' ? undefined : text
}

export const validateRelationsSearch = (search: Record<string, unknown>): RelationsSearch => {
  const q = toSearchText(search.q)
  return {
    ...(isRelationsFilter(search.filtre) && { filtre: search.filtre }),
    ...(q !== undefined && { q }),
  }
}
