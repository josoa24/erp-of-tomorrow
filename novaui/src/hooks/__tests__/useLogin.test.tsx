import { act, renderHook } from '@testing-library/react'
import { login } from '../../services/api/authApi'
import { saveSession } from '../../services/sessionStore'
import { navigateMock, routerWrapper } from '../../test/routerMock'
import { useApiError } from '../useApiError'
import { useLogin } from '../useLogin'

vi.mock('react-router-dom', async (importOriginal) => ({
  ...(await importOriginal<typeof import('react-router-dom')>()),
  useNavigate: () => navigateMock,
}))
vi.mock('../../services/api/authApi', () => ({ login: vi.fn() }))
vi.mock('../../services/sessionStore', () => ({ saveSession: vi.fn() }))
vi.mock('../useApiError', () => ({ useApiError: vi.fn() }))

const handleError = vi.fn()
const credentials = { email: 'rh@erp.local', password: 'secret' }

describe('useLogin', () => {
  beforeEach(() => vi.mocked(useApiError).mockReturnValue(handleError))
  afterEach(() => vi.clearAllMocks())

  it('shouldSaveSessionAndGoHome_whenLoginSucceeds', async () => {
    const session = { token: 't', email: credentials.email, role: 'RH' as const }
    vi.mocked(login).mockResolvedValue(session)
    const { result } = renderHook(() => useLogin(), { wrapper: routerWrapper('/connexion') })

    await act(() => result.current.submit(credentials, true))

    expect(saveSession).toHaveBeenCalledWith(session, true)
    expect(navigateMock).toHaveBeenCalledWith('/', { replace: true })
  })

  it('shouldDelegateToErrorPage_whenLoginFails', async () => {
    const error = new Error('refusé')
    vi.mocked(login).mockRejectedValue(error)
    const { result } = renderHook(() => useLogin(), { wrapper: routerWrapper('/connexion') })

    await act(() => result.current.submit(credentials, false))

    expect(handleError).toHaveBeenCalledWith(error)
    expect(saveSession).not.toHaveBeenCalled()
    expect(result.current.submitting).toBe(false)
  })
})
