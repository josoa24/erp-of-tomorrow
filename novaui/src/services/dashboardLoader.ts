import type { Role } from '../types/auth'
import type { DashboardData, DateRange, PeriodKey, SalesCardData } from '../types/dashboard'
import type { SalesSummary } from '../types/sales'
import { buildDailySeries } from '../utils/dailySeries'
import { getPeriodRange, getPreviousRange } from '../utils/period'
import { canAccessModule } from '../utils/roleAccess'
import { availabilityRate, percentChange } from '../utils/trend'
import { getHrSummary } from './api/rhApi'
import { getStockStatus, getStockSummary } from './api/stockApi'
import { getSalesSummary } from './api/venteApi'
import { buildIssues } from './dashboardIssues'

const whenAllowed = <T>(allowed: boolean, load: () => Promise<T>): Promise<T | null> =>
  allowed ? load() : Promise.resolve(null)

const toSalesCard = (current: SalesSummary, previous: SalesSummary, range: DateRange): SalesCardData => ({
  revenue: current.totalRevenue,
  salesCount: current.salesCount,
  trend: percentChange(current.totalRevenue, previous.totalRevenue),
  daily: buildDailySeries(current.daily, range),
})

export const loadDashboard = async (
  role: Role,
  period: PeriodKey,
  today = new Date(),
): Promise<DashboardData> => {
  const range = getPeriodRange(period, today)
  const canSell = canAccessModule(role, 'vente')
  const canStock = canAccessModule(role, 'stock')

  const [sales, previousSales, stockSummary, stockStatus, hr] = await Promise.all([
    whenAllowed(canSell, () => getSalesSummary(range)),
    whenAllowed(canSell, () => getSalesSummary(getPreviousRange(range))),
    whenAllowed(canStock, getStockSummary),
    whenAllowed(canStock, getStockStatus),
    whenAllowed(canAccessModule(role, 'rh'), getHrSummary),
  ])

  const salesCard = sales && previousSales ? toSalesCard(sales, previousSales, range) : null
  return {
    sales: salesCard,
    stock: stockSummary && {
      availabilityRate: availabilityRate(stockSummary.totalProducts, stockSummary.lowStockProducts),
      lowStock: stockSummary.lowStockProducts,
    },
    hr: hr && { totalEmployees: hr.totalEmployees, onLeaveToday: hr.onLeaveToday },
    issues: buildIssues(stockStatus ?? [], salesCard?.trend ?? null),
  }
}
