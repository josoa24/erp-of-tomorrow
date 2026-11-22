import { futureExp, makeToken } from '../../test/makeToken'
import type { Session } from '../../types/auth'
import { clearSession, readSession, saveSession } from '../sessionStore'

const session = (exp: number): Session => ({
  token: makeToken({ exp }),
  email: 'stock@erp.local',
  fullName: 'Toky Rakoto',
  role: 'STOCK',
})

describe('sessionStore', () => {
  afterEach(() => clearSession())

  it('shouldPersistInLocalStorage_withFullName', () => {
    const value = session(futureExp())

    saveSession(value)

    expect(localStorage.getItem('eclipse.session')).not.toBeNull()
    expect(sessionStorage.getItem('eclipse.session')).toBeNull()
    expect(readSession()).toEqual(value)
  })

  it('shouldStillReadLegacySession_whenStoredInSessionStorage', () => {
    const value = session(futureExp())
    sessionStorage.setItem('eclipse.session', JSON.stringify(value))

    expect(readSession()).toEqual(value)
  })

  it('shouldReturnNullAndClear_whenTokenExpired', () => {
    saveSession(session(1))

    expect(readSession()).toBeNull()
    expect(localStorage.getItem('eclipse.session')).toBeNull()
  })

  it('shouldReturnNull_whenStoredValueIsCorrupted', () => {
    localStorage.setItem('eclipse.session', '{oops')

    expect(readSession()).toBeNull()
  })

  it('shouldReturnNull_whenNothingStored', () => {
    expect(readSession()).toBeNull()
  })
})
