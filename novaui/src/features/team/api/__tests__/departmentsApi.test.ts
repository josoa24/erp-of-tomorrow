import { httpGet } from '../../../../services/api/httpClient'
import { getDepartments } from '../departmentsApi'

vi.mock('../../../../services/api/httpClient', () => ({ httpGet: vi.fn().mockResolvedValue([]) }))

describe('departmentsApi', () => {
  it('shouldReadDepartments', async () => {
    await getDepartments()

    expect(httpGet).toHaveBeenCalledWith('/api/rh/departments', {})
  })
})
