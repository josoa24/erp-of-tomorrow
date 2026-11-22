export type PeriodKey = 'today' | 'week' | 'month'

export interface DateRange {
  from: string
  to: string
}

export interface SalesCardData {
  revenue: number
  salesCount: number
  trend: number | null
  daily: number[]
}

export interface StockCardData {
  availabilityRate: number | null
  lowStock: number
}

export interface HrCardData {
  totalEmployees: number
  onLeaveToday: number
}

export interface Issue {
  id: string
  label: string
  severity: 'critical' | 'warning'
}

export interface DashboardData {
  sales: SalesCardData | null
  stock: StockCardData | null
  hr: HrCardData | null
  issues: Issue[]
}
