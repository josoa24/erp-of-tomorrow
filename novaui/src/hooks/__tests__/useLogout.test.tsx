import { renderHook } from '@testing-library/react'
import { clearSession } from '../../services/sessionStore'
import { navigateMock, routerWrapper } from '../../test/routerMock'
import { useLogout } from '../useLogout'

vi.mock('react-router-dom', async (importOriginal) => ({
  ...(await importOriginal<typeof import('react-router-dom')>()),
  useNavigate: () => navigateMock,
}))
vi.mock('../../services/sessionStore', () => ({ clearSession: vi.fn() }))

describe('useLogout', () => {
  it('shouldClearSessionAndGoToLogin', () => {
    const { result } = renderHook(() => useLogout(), { wrapper: routerWrapper() })

    result.current()

    expect(clearSession).toHaveBeenCalled()
    expect(navigateMock).toHaveBeenCalledWith('/connexion', { replace: true })
  })
})
