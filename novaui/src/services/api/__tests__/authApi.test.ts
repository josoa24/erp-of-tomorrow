import { getCurrentAccount, login } from '../authApi'
import { httpGet, httpPost } from '../httpClient'

vi.mock('../httpClient', () => ({ httpPost: vi.fn(), httpGet: vi.fn() }))

describe('authApi', () => {
  it('shouldPostCredentialsToLoginEndpoint', async () => {
    vi.mocked(httpPost).mockResolvedValue({ token: 't', email: 'a@b.c', fullName: 'Hery Rabe', role: 'RH' })

    const result = await login({ email: 'a@b.c', password: 'secret' })

    expect(httpPost).toHaveBeenCalledWith('/api/auth/login', { email: 'a@b.c', password: 'secret' })
    expect(result.fullName).toBe('Hery Rabe')
  })

  it('shouldReadOwnAccount_fromUsersMe', async () => {
    const signal = new AbortController().signal
    vi.mocked(httpGet).mockResolvedValue({ id: 1, email: 'a@b.c', fullName: null, role: 'VENTE' })

    const account = await getCurrentAccount({ signal })

    expect(httpGet).toHaveBeenCalledWith('/api/auth/users/me', { signal })
    expect(account.role).toBe('VENTE')
  })
})
