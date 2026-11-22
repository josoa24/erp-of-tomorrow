import { httpDelete, httpGet, httpGetPage, httpPost, httpPut } from '../../../../services/api/httpClient'
import { createSale, deleteSale, getSale, listSales, updateSale, validateSale } from '../salesApi'

vi.mock('../../../../services/api/httpClient', () => ({
  httpGet: vi.fn(),
  httpGetPage: vi.fn(),
  httpPost: vi.fn(),
  httpPut: vi.fn(),
  httpDelete: vi.fn(),
}))

const REQUEST = { customerId: 4, lines: [{ productId: 7, quantity: 2 }], toDeliver: false }

describe('salesApi', () => {
  afterEach(() => vi.clearAllMocks())

  it('shouldSendFiltersAndPage_whenListingSales', async () => {
    const signal = new AbortController().signal

    await listSales(
      { search: 'Rova', paymentStatus: ['TO_COLLECT', 'OVERDUE'], page: 1, size: 20 },
      { signal },
    )

    expect(httpGetPage).toHaveBeenCalledWith(
      '/api/vente/sales?search=Rova&paymentStatus=TO_COLLECT&paymentStatus=OVERDUE&page=1&size=20',
      { signal },
    )
  })

  it('shouldSendPaymentMethod_whenListingCreditSales', async () => {
    await listSales({ paymentMethod: 'CREDIT', page: 0, size: 20 })

    expect(httpGetPage).toHaveBeenCalledWith('/api/vente/sales?paymentMethod=CREDIT&page=0&size=20', {})
  })

  it('shouldReadOneSale', async () => {
    await getSale(12)

    expect(httpGet).toHaveBeenCalledWith('/api/vente/sales/12', {})
  })

  it('shouldPostTheRequest_whenCreatingSale', async () => {
    await createSale(REQUEST)

    expect(httpPost).toHaveBeenCalledWith('/api/vente/sales', REQUEST)
  })

  it('shouldPutTheRequest_whenUpdatingDraft', async () => {
    await updateSale(3, REQUEST)

    expect(httpPut).toHaveBeenCalledWith('/api/vente/sales/3', REQUEST)
  })

  it('shouldDeleteDraft', async () => {
    await deleteSale(3)

    expect(httpDelete).toHaveBeenCalledWith('/api/vente/sales/3')
  })

  it('shouldPostValidate', async () => {
    await validateSale(3)

    expect(httpPost).toHaveBeenCalledWith('/api/vente/sales/3/validate')
  })
})
