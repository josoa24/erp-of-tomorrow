import { defaultStringifySearch } from '@tanstack/react-router'

const isStringList = (value: unknown): value is string[] =>
  Array.isArray(value) && value.length > 0 && value.every((item) => typeof item === 'string')

export const stringifySearch = (search: Record<string, unknown>): string => {
  const scalars = Object.fromEntries(Object.entries(search).filter(([, value]) => !isStringList(value)))
  const lists = new URLSearchParams()
  Object.entries(search).forEach(([key, value]) => {
    if (isStringList(value)) value.forEach((item) => lists.append(key, item))
  })
  const base = defaultStringifySearch(scalars).replace(/^\?/, '')
  const query = [base, lists.toString()].filter(Boolean).join('&')
  return query ? `?${query}` : ''
}
