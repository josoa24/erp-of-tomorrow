import { httpGet, httpPost } from '../../../../services/api/httpClient'
import { getProductLots } from '../stockLotsApi'
import { recordMovement } from '../stockMovementsApi'
import { searchStockProducts } from '../stockProductsApi'

vi.mock('../../../../services/api/httpClient', () => ({ httpGet: vi.fn(), httpPost: vi.fn() }))

describe('stock product resources', () => {
  afterEach(() => vi.clearAllMocks())

  it('shouldSearchProductsByName', async () => {
    vi.mocked(httpGet).mockResolvedValue([])

    await searchStockProducts('huile')

    expect(httpGet).toHaveBeenCalledWith('/api/stock/products?search=huile', {})
  })

  it('shouldPostMovementOnTheProduct', async () => {
    const movement = { type: 'IN' as const, quantity: 12, lotNumber: 'LOT-2611-A', expiryDate: '2027-02-12' }
    vi.mocked(httpPost).mockResolvedValue({ id: 9 })

    await recordMovement(4, movement)

    expect(httpPost).toHaveBeenCalledWith('/api/stock/products/4/movements', movement)
  })

  it('shouldReadLotsOfTheProduct', async () => {
    const signal = new AbortController().signal
    vi.mocked(httpGet).mockResolvedValue([])

    await getProductLots(7, { signal })

    expect(httpGet).toHaveBeenCalledWith('/api/stock/products/7/lots', { signal })
  })
})
