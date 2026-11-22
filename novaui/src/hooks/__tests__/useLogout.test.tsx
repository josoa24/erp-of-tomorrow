import { renderHook } from '@testing-library/react'
import { clearSession } from '../../services/sessionStore'
import { mockRouter, navigateMock } from '../../test/routerMock'
import { useLogout } from '../useLogout'

vi.mock('@tanstack/react-router', () => mockRouter())
vi.mock('../../services/sessionStore', () => ({ clearSession: vi.fn() }))

describe('useLogout', () => {
  it('shouldClearSessionAndGoToLogin', () => {
    const { result } = renderHook(() => useLogout())

    result.current()

    expect(clearSession).toHaveBeenCalled()
    expect(navigateMock).toHaveBeenCalledWith({ to: '/connexion', replace: true })
  })
})
