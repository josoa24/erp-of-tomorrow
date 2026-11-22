import { getHrSummary } from '../api/rhApi'
import { getStockStatus, getStockSummary } from '../api/stockApi'
import { getSalesSummary } from '../api/venteApi'
import { loadDashboard } from '../dashboardLoader'

vi.mock('../api/rhApi', () => ({ getHrSummary: vi.fn() }))
vi.mock('../api/stockApi', () => ({ getStockSummary: vi.fn(), getStockStatus: vi.fn() }))
vi.mock('../api/venteApi', () => ({ getSalesSummary: vi.fn() }))

const today = new Date(2026, 10, 3)
const sales = (totalRevenue: number) => ({
  salesCount: 2,
  totalRevenue,
  averageBasket: 0,
  daily: [{ date: '2026-11-02', salesCount: 2, revenue: totalRevenue }],
})

describe('loadDashboard', () => {
  beforeEach(() => {
    vi.mocked(getSalesSummary).mockImplementation(async ({ from }) =>
      sales(from === '2026-11-01' ? 1200 : 1000),
    )
    vi.mocked(getStockSummary).mockResolvedValue({ totalProducts: 4, lowStockProducts: 1, totalStockValue: 0 })
    vi.mocked(getStockStatus).mockResolvedValue([])
    vi.mocked(getHrSummary).mockResolvedValue({ totalEmployees: 12, onLeaveToday: 2 })
  })

  afterEach(() => vi.clearAllMocks())

  it('shouldBuildEveryCard_whenAdmin', async () => {
    const data = await loadDashboard('ADMIN', 'month', today)

    expect(data.sales).toEqual({ revenue: 1200, salesCount: 2, trend: 20, daily: [0, 1200, 0] })
    expect(data.stock).toEqual({ availabilityRate: 75, lowStock: 1 })
    expect(data.hr).toEqual({ totalEmployees: 12, onLeaveToday: 2 })
    expect(getSalesSummary).toHaveBeenCalledWith({ from: '2026-10-29', to: '2026-10-31' })
  })

  it('shouldOnlyCallAllowedModules_whenBusinessRole', async () => {
    const data = await loadDashboard('STOCK', 'month', today)

    expect(data.sales).toBeNull()
    expect(data.hr).toBeNull()
    expect(data.stock).not.toBeNull()
    expect(getSalesSummary).not.toHaveBeenCalled()
    expect(getHrSummary).not.toHaveBeenCalled()
  })

  it('shouldPropagateError_whenModuleFails', async () => {
    vi.mocked(getHrSummary).mockRejectedValue(new Error('down'))

    await expect(loadDashboard('RH', 'week', today)).rejects.toThrow('down')
  })
})
