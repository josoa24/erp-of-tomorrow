import { httpGet } from '../../../../services/api/httpClient'
import { getModuleDefinitions } from '../moduleDefinitionsApi'

vi.mock('../../../../services/api/httpClient', () => ({ httpGet: vi.fn() }))

describe('getModuleDefinitions', () => {
  it('shouldGetDefinitionsThroughTheGateway', async () => {
    const signal = new AbortController().signal
    vi.mocked(httpGet).mockResolvedValue([])

    await getModuleDefinitions({ signal })

    expect(httpGet).toHaveBeenCalledWith('/api/modules/definitions', { signal })
  })
})
