import { renderHook } from '@testing-library/react'
import { clearSession } from '../../services/sessionStore'
import { navigateMock, routerWrapper } from '../../test/routerMock'
import { ApiError } from '../../types/apiError'
import { useApiError } from '../useApiError'

vi.mock('react-router-dom', async (importOriginal) => ({
  ...(await importOriginal<typeof import('react-router-dom')>()),
  useNavigate: () => navigateMock,
}))
vi.mock('../../services/sessionStore', () => ({ clearSession: vi.fn() }))

describe('useApiError', () => {
  afterEach(() => vi.clearAllMocks())

  it('shouldOpenErrorPage_withTypeMessageAndOrigin', () => {
    const { result } = renderHook(() => useApiError(), { wrapper: routerWrapper('/stock') })

    result.current(new ApiError('forbidden', 403, 'Interdit'))

    expect(navigateMock).toHaveBeenCalledWith('/erreur', {
      state: { type: 'forbidden', message: 'Interdit', from: '/stock' },
    })
    expect(clearSession).not.toHaveBeenCalled()
  })

  it('shouldClearSession_whenUnauthorized', () => {
    const { result } = renderHook(() => useApiError(), { wrapper: routerWrapper() })

    result.current(new ApiError('unauthorized', 401, 'Session expirée'))

    expect(clearSession).toHaveBeenCalled()
  })
})
