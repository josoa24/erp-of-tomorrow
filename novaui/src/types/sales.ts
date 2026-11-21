export interface DailyRevenue {
  date: string
  salesCount: number
  revenue: number
}

export interface SalesSummary {
  salesCount: number
  totalRevenue: number
  averageBasket: number
  daily: DailyRevenue[]
}
