import { httpGet, httpPost } from '../../../../services/api/httpClient'
import { createEmployee, getEmployeeCounts, getEmployees } from '../employeesApi'

vi.mock('../../../../services/api/httpClient', () => ({
  httpGet: vi.fn().mockResolvedValue([]),
  httpPost: vi.fn().mockResolvedValue({}),
}))

describe('employeesApi', () => {
  afterEach(() => vi.clearAllMocks())

  it('shouldSendSearchTerm_whenSearching', async () => {
    const signal = new AbortController().signal

    await getEmployees('rakoto', { signal })

    expect(httpGet).toHaveBeenCalledWith('/api/rh/employees?search=rakoto', { signal })
  })

  it('shouldOmitSearch_whenTermIsEmpty', async () => {
    await getEmployees('')

    expect(httpGet).toHaveBeenCalledWith('/api/rh/employees', {})
  })

  it('shouldReadCounts', async () => {
    await getEmployeeCounts()

    expect(httpGet).toHaveBeenCalledWith('/api/rh/employees/counts', {})
  })

  it('shouldPostEmployee', async () => {
    const request = {
      firstName: 'Toky',
      lastName: 'Rakoto',
      email: 'toky.rakoto@boutique-tana.mg',
      departmentId: 2,
      hireDate: '2021-03-01',
      baseSalary: 800000,
    }

    await createEmployee(request)

    expect(httpPost).toHaveBeenCalledWith('/api/rh/employees', request)
  })
})
