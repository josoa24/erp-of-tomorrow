import { renderHook } from '@testing-library/react'
import { queryClient } from '../../lib/queryClient'
import { clearSession } from '../../services/sessionStore'
import { mockRouter, navigateMock } from '../../test/routerMock'
import { useLogout } from '../useLogout'

vi.mock('@tanstack/react-router', () => mockRouter())
vi.mock('../../services/sessionStore', () => ({ clearSession: vi.fn() }))

describe('useLogout', () => {
  it('shouldClearSessionAndCacheThenGoToLogin', () => {
    const clearCache = vi.spyOn(queryClient, 'clear')
    const { result } = renderHook(() => useLogout())

    result.current()

    expect(clearSession).toHaveBeenCalled()
    expect(clearCache).toHaveBeenCalled()
    expect(navigateMock).toHaveBeenCalledWith({ to: '/connexion', replace: true })
  })
})
