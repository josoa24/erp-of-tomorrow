import { httpGet, httpPost } from '../../../../services/api/httpClient'
import { createContact, getContactCounts, getContacts, getContactSummary } from '../contactsApi'

vi.mock('../../../../services/api/httpClient', () => ({
  httpGet: vi.fn().mockResolvedValue([]),
  httpPost: vi.fn().mockResolvedValue({}),
}))

describe('contactsApi', () => {
  afterEach(() => vi.clearAllMocks())

  it('shouldListAllContacts_whenNoFilterIsSet', async () => {
    await getContacts({ kind: 'ALL' })

    expect(httpGet).toHaveBeenCalledWith('/api/crm/contacts?kind=ALL', {})
  })

  it('shouldSendSearchAndFlags_whenFiltersAreSet', async () => {
    await getContacts({ kind: 'CUSTOMER', search: 'Rova', followUp: true, professional: true })

    expect(httpGet).toHaveBeenCalledWith(
      '/api/crm/contacts?kind=CUSTOMER&search=Rova&followUp=true&professional=true',
      {},
    )
  })

  it('shouldOmitFalseFlags_whenFiltersAreOff', async () => {
    await getContacts({ kind: 'SUPPLIER', followUp: false, professional: false })

    expect(httpGet).toHaveBeenCalledWith('/api/crm/contacts?kind=SUPPLIER', {})
  })

  it('shouldCallCountsWithSearch_whenSearchIsGiven', async () => {
    await getContactCounts('Toamasina')

    expect(httpGet).toHaveBeenCalledWith('/api/crm/contacts/counts?search=Toamasina', {})
  })

  it('shouldCallCountsWithoutQuery_whenSearchIsEmpty', async () => {
    await getContactCounts('')

    expect(httpGet).toHaveBeenCalledWith('/api/crm/contacts/counts', {})
  })

  it('shouldCallSummaryEndpoint', async () => {
    await getContactSummary()

    expect(httpGet).toHaveBeenCalledWith('/api/crm/contacts/summary', {})
  })

  it('shouldPostNewContact', async () => {
    const request = { kind: 'SUPPLIER' as const, name: 'Tiko Distribution', city: 'Antananarivo' }

    await createContact(request)

    expect(httpPost).toHaveBeenCalledWith('/api/crm/contacts', request)
  })
})
