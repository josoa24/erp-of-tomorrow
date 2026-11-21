import { login } from '../authApi'
import { httpPost } from '../httpClient'

vi.mock('../httpClient', () => ({ httpPost: vi.fn() }))

describe('authApi', () => {
  it('shouldPostCredentialsToLoginEndpoint', async () => {
    vi.mocked(httpPost).mockResolvedValue({ token: 't', email: 'a@b.c', role: 'RH' })

    const result = await login({ email: 'a@b.c', password: 'secret' })

    expect(httpPost).toHaveBeenCalledWith('/api/auth/login', { email: 'a@b.c', password: 'secret' })
    expect(result.role).toBe('RH')
  })
})
