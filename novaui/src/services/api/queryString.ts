export type QueryScalar = string | number | boolean
export type QueryValue = QueryScalar | readonly QueryScalar[] | null | undefined

const isEmpty = (value: QueryValue): value is null | undefined | '' =>
  value === null || value === undefined || value === ''

const toList = (value: QueryScalar | readonly QueryScalar[]): readonly QueryScalar[] =>
  typeof value === 'object' ? value : [value]

export const buildQuery = (params: Record<string, QueryValue>): string => {
  const search = new URLSearchParams()
  Object.entries(params).forEach(([key, value]) => {
    if (isEmpty(value)) return
    toList(value).forEach((item) => search.append(key, String(item)))
  })
  const query = search.toString()
  return query ? `?${query}` : ''
}
