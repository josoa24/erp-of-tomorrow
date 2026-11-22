import { act, renderHook } from '@testing-library/react'
import { login } from '../../services/api/authApi'
import { forgetLoginEmail, readLoginEmail, rememberLoginEmail } from '../../services/loginEmailStore'
import { saveSession } from '../../services/sessionStore'
import { mockRouter, navigateMock } from '../../test/routerMock'
import { useApiError } from '../useApiError'
import { useLogin } from '../useLogin'

vi.mock('@tanstack/react-router', () => mockRouter('/connexion'))
vi.mock('../../services/api/authApi', () => ({ login: vi.fn() }))
vi.mock('../../services/sessionStore', () => ({ saveSession: vi.fn() }))
vi.mock('../../services/loginEmailStore', () => ({
  readLoginEmail: vi.fn(() => 'hery@boutique-tana.mg'),
  rememberLoginEmail: vi.fn(),
  forgetLoginEmail: vi.fn(),
}))
vi.mock('../useApiError', () => ({ useApiError: vi.fn() }))

const handleError = vi.fn()
const credentials = { email: 'rasoa@boutique-tana.mg', password: 'secret' }
const session = { token: 't', email: credentials.email, fullName: 'Rasoa Andria', role: 'VENTE' as const }

describe('useLogin', () => {
  beforeEach(() => vi.mocked(useApiError).mockReturnValue(handleError))
  afterEach(() => vi.clearAllMocks())

  it('shouldSaveSessionInBrowserAndGoHome_whenLoginSucceeds', async () => {
    vi.mocked(login).mockResolvedValue(session)
    const { result } = renderHook(() => useLogin())

    await act(() => result.current.submit(credentials))

    expect(saveSession).toHaveBeenCalledWith(session)
    expect(forgetLoginEmail).toHaveBeenCalled()
    expect(navigateMock).toHaveBeenCalledWith({ to: '/', replace: true })
  })

  it('shouldRememberEmailAndOpenErrorPage_whenLoginIsRefused', async () => {
    const error = new Error('refusé')
    vi.mocked(login).mockRejectedValue(error)
    const { result } = renderHook(() => useLogin())

    await act(() => result.current.submit(credentials))

    expect(rememberLoginEmail).toHaveBeenCalledWith('rasoa@boutique-tana.mg')
    expect(handleError).toHaveBeenCalledWith(error)
    expect(saveSession).not.toHaveBeenCalled()
    expect(result.current.submitting).toBe(false)
  })

  it('shouldIgnoreSecondSubmit_whileFirstIsPending', async () => {
    vi.mocked(login).mockReturnValue(new Promise(() => undefined))
    const { result } = renderHook(() => useLogin())

    act(() => void result.current.submit(credentials))
    act(() => void result.current.submit(credentials))

    expect(login).toHaveBeenCalledTimes(1)
    expect(result.current.submitting).toBe(true)
  })

  it('shouldExposeRememberedEmail_asInitialValue', () => {
    const { result } = renderHook(() => useLogin())

    expect(readLoginEmail).toHaveBeenCalled()
    expect(result.current.initialEmail).toBe('hery@boutique-tana.mg')
  })
})
