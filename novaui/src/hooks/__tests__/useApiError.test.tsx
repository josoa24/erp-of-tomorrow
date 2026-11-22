import { renderHook } from '@testing-library/react'
import { clearSession } from '../../services/sessionStore'
import { mockRouter, navigateMock } from '../../test/routerMock'
import { ApiError } from '../../types/apiError'
import { useApiError } from '../useApiError'

vi.mock('@tanstack/react-router', () => mockRouter('/stock'))
vi.mock('../../services/sessionStore', () => ({ clearSession: vi.fn() }))

describe('useApiError', () => {
  afterEach(() => vi.clearAllMocks())

  it('shouldOpenErrorPage_withTypeMessageOriginStatusAndCode', () => {
    const { result } = renderHook(() => useApiError())

    result.current(new ApiError('forbidden', 403, 'Interdit', 'FORBIDDEN'))

    expect(navigateMock).toHaveBeenCalledWith({
      to: '/erreur',
      state: { error: { type: 'forbidden', message: 'Interdit', from: '/stock', status: 403, code: 'FORBIDDEN' } },
    })
    expect(clearSession).not.toHaveBeenCalled()
  })

  it('shouldOmitStatusAndCode_whenTheServerWasUnreachable', () => {
    const { result } = renderHook(() => useApiError())

    result.current(new ApiError('network', 0, 'Injoignable'))

    expect(navigateMock.mock.calls[0][0].state.error).toEqual({
      type: 'network',
      message: 'Injoignable',
      from: '/stock',
    })
  })

  it('shouldClearSession_whenUnauthorized', () => {
    const { result } = renderHook(() => useApiError())

    result.current(new ApiError('unauthorized', 401, 'Session expirée'))

    expect(clearSession).toHaveBeenCalled()
  })
})
