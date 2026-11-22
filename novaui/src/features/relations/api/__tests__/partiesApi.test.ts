import { httpGet, httpPost } from '../../../../services/api/httpClient'
import { getTimeline, recordInteraction } from '../partiesApi'

vi.mock('../../../../services/api/httpClient', () => ({
  httpGet: vi.fn().mockResolvedValue([]),
  httpPost: vi.fn().mockResolvedValue({}),
}))

describe('partiesApi', () => {
  it('shouldPostInteractionOnTheParty', async () => {
    const request = { type: 'APPEL' as const, summary: 'Relance de la commande de riz' }

    await recordInteraction({ kind: 'CUSTOMER', partyId: 7 }, request)

    expect(httpPost).toHaveBeenCalledWith('/api/crm/parties/CUSTOMER/7/interactions', request)
  })

  it('shouldReadTimelineOfTheParty', async () => {
    await getTimeline({ kind: 'SUPPLIER', partyId: 3 })

    expect(httpGet).toHaveBeenCalledWith('/api/crm/parties/SUPPLIER/3/timeline', {})
  })
})
