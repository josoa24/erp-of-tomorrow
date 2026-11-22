import { httpGet, httpGetPage, httpPost } from '../../../../services/api/httpClient'
import { searchCatalogProducts } from '../catalogApi'
import { searchCustomerOptions } from '../customerOptionsApi'
import { cancelSale } from '../saleCancellationApi'
import { listSalePayments, recordSalePayment } from '../salePaymentsApi'
import { getCreditSummary, getSaleCounts, getSalesKpis } from '../salesReportsApi'

vi.mock('../../../../services/api/httpClient', () => ({
  httpGet: vi.fn(),
  httpGetPage: vi.fn(),
  httpPost: vi.fn(),
}))

describe('sales support api', () => {
  afterEach(() => vi.clearAllMocks())

  it.each([
    ['/api/vente/sales/counts', getSaleCounts],
    ['/api/vente/sales/kpis', getSalesKpis],
    ['/api/vente/sales/credit-summary', getCreditSummary],
  ])('shouldReadReport_%s', async (path, read) => {
    await read()

    expect(httpGet).toHaveBeenCalledWith(path, {})
  })

  it('shouldListAndRecordPayments', async () => {
    await listSalePayments(5)
    await recordSalePayment(5, { amount: 20000, method: 'MVOLA' })

    expect(httpGet).toHaveBeenCalledWith('/api/vente/sales/5/payments', {})
    expect(httpPost).toHaveBeenCalledWith('/api/vente/sales/5/payments', { amount: 20000, method: 'MVOLA' })
  })

  it('shouldPostReason_whenCancellingSale', async () => {
    await cancelSale(5, { reason: 'Erreur de saisie' })

    expect(httpPost).toHaveBeenCalledWith('/api/vente/sales/5/cancel', { reason: 'Erreur de saisie' })
  })

  it('shouldSearchCatalogProducts', async () => {
    await searchCatalogProducts('riz')

    expect(httpGet).toHaveBeenCalledWith('/api/vente/catalog/products?search=riz', {})
  })

  it('shouldReturnFirstCustomers_whenSearchingCustomers', async () => {
    const hery = { id: 1, name: 'Hery Rabe', city: 'Antananarivo', phone: null }
    vi.mocked(httpGetPage).mockResolvedValue({ items: [hery], total: 1 })

    const customers = await searchCustomerOptions('her')

    expect(httpGetPage).toHaveBeenCalledWith('/api/vente/customers?search=her&page=0&size=6', {})
    expect(customers).toEqual([hery])
  })
})
