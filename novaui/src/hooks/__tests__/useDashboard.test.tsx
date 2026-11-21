import { renderHook, waitFor } from '@testing-library/react'
import { loadDashboard } from '../../services/dashboardLoader'
import { readSession } from '../../services/sessionStore'
import type { DashboardData } from '../../types/dashboard'
import { useApiError } from '../useApiError'
import { useDashboard } from '../useDashboard'

vi.mock('../../services/dashboardLoader', () => ({ loadDashboard: vi.fn() }))
vi.mock('../../services/sessionStore', () => ({ readSession: vi.fn() }))
vi.mock('../useApiError', () => ({ useApiError: vi.fn() }))

const handleError = vi.fn()
const data: DashboardData = { sales: null, stock: null, hr: null, issues: [] }

describe('useDashboard', () => {
  beforeEach(() => {
    vi.mocked(useApiError).mockReturnValue(handleError)
    vi.mocked(readSession).mockReturnValue({ token: 't', email: 'a@b.c', role: 'VENTE' })
  })

  afterEach(() => vi.clearAllMocks())

  it('shouldExposeData_afterLoading', async () => {
    vi.mocked(loadDashboard).mockResolvedValue(data)

    const { result } = renderHook(() => useDashboard('week'))

    expect(result.current.loading).toBe(true)
    await waitFor(() => expect(result.current.loading).toBe(false))
    expect(result.current.data).toBe(data)
    expect(loadDashboard).toHaveBeenCalledWith('VENTE', 'week')
  })

  it('shouldDelegateError_whenLoadingFails', async () => {
    const error = new Error('down')
    vi.mocked(loadDashboard).mockRejectedValue(error)

    renderHook(() => useDashboard('month'))

    await waitFor(() => expect(handleError).toHaveBeenCalledWith(error))
  })

  it('shouldNotLoad_whenNoSession', () => {
    vi.mocked(readSession).mockReturnValue(null)

    renderHook(() => useDashboard('month'))

    expect(loadDashboard).not.toHaveBeenCalled()
  })
})
