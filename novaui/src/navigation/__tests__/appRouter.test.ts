import { createMemoryHistory } from '@tanstack/react-router'
import { readSession } from '../../services/sessionStore'
import { createAppRouter } from '../appRouter'

vi.mock('../../services/sessionStore', () => ({ readSession: vi.fn() }))

const session = { token: 't', email: 'rh@erp.local', role: 'RH' as const }

const loadAt = async (path: string) => {
  const router = createAppRouter(createMemoryHistory({ initialEntries: [path] }))
  await router.load()
  return router.state.location.pathname
}

describe('appRouter', () => {
  afterEach(() => vi.clearAllMocks())

  it('shouldRedirectToLogin_whenNoSession', async () => {
    vi.mocked(readSession).mockReturnValue(null)

    expect(await loadAt('/')).toBe('/connexion')
  })

  it('shouldKeepHome_whenSessionExists', async () => {
    vi.mocked(readSession).mockReturnValue(session)

    expect(await loadAt('/')).toBe('/')
  })

  it('shouldRedirectLoginToHome_whenSessionExists', async () => {
    vi.mocked(readSession).mockReturnValue(session)

    expect(await loadAt('/connexion')).toBe('/')
  })

  it('shouldKeepErrorPageReachable_whenNoSession', async () => {
    vi.mocked(readSession).mockReturnValue(null)

    expect(await loadAt('/erreur')).toBe('/erreur')
  })
})
