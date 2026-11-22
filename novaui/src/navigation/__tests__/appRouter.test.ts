import { createMemoryHistory } from '@tanstack/react-router'
import { readSession } from '../../services/sessionStore'
import type { Role } from '../../types/auth'
import { createAppRouter } from '../appRouter'

vi.mock('../../services/sessionStore', () => ({ readSession: vi.fn() }))

const sessionFor = (role: Role) => ({ token: 't', email: 'hery@boutique-tana.mg', role })

const loadAt = async (path: string) => {
  const router = createAppRouter(createMemoryHistory({ initialEntries: [path] }))
  await router.load()
  return router.state.location
}

describe('appRouter', () => {
  afterEach(() => vi.clearAllMocks())

  it('shouldRedirectToLogin_whenNoSession', async () => {
    vi.mocked(readSession).mockReturnValue(null)

    expect((await loadAt('/stock')).pathname).toBe('/connexion')
  })

  it('shouldKeepHome_whenSessionExists', async () => {
    vi.mocked(readSession).mockReturnValue(sessionFor('RH'))

    expect((await loadAt('/')).pathname).toBe('/')
  })

  it('shouldRedirectLoginToHome_whenSessionExists', async () => {
    vi.mocked(readSession).mockReturnValue(sessionFor('RH'))

    expect((await loadAt('/connexion')).pathname).toBe('/')
  })

  it('shouldKeepErrorPageReachable_whenNoSession', async () => {
    vi.mocked(readSession).mockReturnValue(null)

    expect((await loadAt('/erreur')).pathname).toBe('/erreur')
  })

  it.each([
    ['ADMIN', '/stock'],
    ['VENTE', '/ventes'],
    ['STOCK', '/stock'],
    ['RH', '/equipe'],
    ['CRM', '/relations'],
    ['VENTE', '/profil'],
    ['CRM', '/modules/livraisons'],
  ] as const)('shouldOpenModule_whenRole_%s_visits_%s', async (role, path) => {
    vi.mocked(readSession).mockReturnValue(sessionFor(role))

    expect((await loadAt(path)).pathname).toBe(path)
  })

  it('shouldSendToForbiddenErrorPage_whenRoleCannotUseTheModule', async () => {
    vi.mocked(readSession).mockReturnValue(sessionFor('VENTE'))

    const location = await loadAt('/stock')

    expect(location.pathname).toBe('/erreur')
    expect(location.state.error).toMatchObject({ type: 'forbidden', status: 403, code: 'FORBIDDEN', from: '/' })
  })

  it('shouldKeepCreateModuleRequest_inTheSearch', async () => {
    vi.mocked(readSession).mockReturnValue(sessionFor('ADMIN'))

    expect((await loadAt('/stock?creer=module')).search).toEqual({ creer: 'module' })
  })
})
