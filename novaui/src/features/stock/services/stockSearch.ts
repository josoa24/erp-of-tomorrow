import { isStockFilter, type StockFilter } from './stockFilters'

export interface StockSearch {
  filtre?: Exclude<StockFilter, 'tous'>
  recherche?: string
}

const toFilter = (value: unknown): StockSearch['filtre'] =>
  isStockFilter(value) && value !== 'tous' ? value : undefined

const toText = (value: unknown): string | undefined => {
  if (typeof value !== 'string' && typeof value !== 'number') return undefined
  const text = String(value).trim()
  return text === '' ? undefined : text
}

export const validateStockSearch = (search: Record<string, unknown>): StockSearch => {
  const filtre = toFilter(search.filtre)
  const recherche = toText(search.recherche)
  return { ...(filtre && { filtre }), ...(recherche && { recherche }) }
}
