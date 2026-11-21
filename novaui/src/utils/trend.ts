export const percentChange = (current: number, previous: number): number | null => {
  if (previous <= 0) return null
  return Math.round(((current - previous) / previous) * 1000) / 10
}

export const availabilityRate = (totalProducts: number, lowStockProducts: number): number | null => {
  if (totalProducts <= 0) return null
  return Math.round(((totalProducts - lowStockProducts) / totalProducts) * 1000) / 10
}
