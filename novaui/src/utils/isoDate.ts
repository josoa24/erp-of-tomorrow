export const DAY_MS = 86_400_000

const pad = (value: number): string => String(value).padStart(2, '0')

export const toLocalIsoDate = (date: Date): string =>
  `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`

export const toUtcTime = (isoDate: string): number => Date.parse(`${isoDate}T00:00:00Z`)

export const fromUtcTime = (time: number): string => new Date(time).toISOString().slice(0, 10)
