export interface SaleCounts {
  all: number
  toCollect: number
  paid: number
  cancelled: number
  overdue: number
  credit: number
  draft: number
}

export interface SalesKpis {
  monthRevenue: number
  previousMonthRevenue: number
  todaySalesCount: number
  todayRevenue: number
  todayCollected: number
  sameWeekdayLastWeekRevenue: number
  averageBasket: number
  previousMonthAverageBasket: number
}

export interface CreditSummary {
  outstanding: number
  customers: number
  overdueCustomers: number
}
