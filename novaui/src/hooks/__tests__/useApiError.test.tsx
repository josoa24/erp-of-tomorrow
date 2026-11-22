import { renderHook } from '@testing-library/react'
import { clearSession } from '../../services/sessionStore'
import { mockRouter, navigateMock } from '../../test/routerMock'
import { ApiError } from '../../types/apiError'
import { useApiError } from '../useApiError'

vi.mock('@tanstack/react-router', () => mockRouter('/stock'))
vi.mock('../../services/sessionStore', () => ({ clearSession: vi.fn() }))

describe('useApiError', () => {
  afterEach(() => vi.clearAllMocks())

  it('shouldOpenErrorPage_withTypeMessageAndOrigin', () => {
    const { result } = renderHook(() => useApiError())

    result.current(new ApiError('forbidden', 403, 'Interdit'))

    expect(navigateMock).toHaveBeenCalledWith({
      to: '/erreur',
      state: { error: { type: 'forbidden', message: 'Interdit', from: '/stock' } },
    })
    expect(clearSession).not.toHaveBeenCalled()
  })

  it('shouldClearSession_whenUnauthorized', () => {
    const { result } = renderHook(() => useApiError())

    result.current(new ApiError('unauthorized', 401, 'Session expirée'))

    expect(clearSession).toHaveBeenCalled()
  })
})
