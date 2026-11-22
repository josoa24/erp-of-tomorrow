import { renderHook } from '@testing-library/react'
import { readSession } from '../../services/sessionStore'
import { useCurrentUser } from '../useCurrentUser'

vi.mock('../../services/sessionStore', () => ({ readSession: vi.fn() }))

describe('useCurrentUser', () => {
  it('shouldReturnIdentity_whenSessionExists', () => {
    vi.mocked(readSession).mockReturnValue({ token: 't', email: 'vente@erp.local', role: 'VENTE' })

    const { result } = renderHook(() => useCurrentUser())

    expect(result.current).toMatchObject({ displayName: 'Vente', roleLabel: 'Ventes', initials: 'V' })
  })

  it('shouldReturnNull_whenNoSession', () => {
    vi.mocked(readSession).mockReturnValue(null)

    expect(renderHook(() => useCurrentUser()).result.current).toBeNull()
  })
})
