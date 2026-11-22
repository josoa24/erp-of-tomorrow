export const TOTAL_COUNT_HEADER = 'X-Total-Count'

export const parseBody = async <T>(response: Response): Promise<T> => {
  if (response.status === 204) return undefined as T
  const text = await response.text()
  return (text ? JSON.parse(text) : undefined) as T
}

export const readTotalCount = (headers: Headers): number | null => {
  const raw = headers.get(TOTAL_COUNT_HEADER)
  if (raw === null || raw.trim() === '') return null
  const total = Number(raw)
  return Number.isInteger(total) && total >= 0 ? total : null
}
