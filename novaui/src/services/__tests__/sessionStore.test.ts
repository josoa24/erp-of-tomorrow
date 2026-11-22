import { futureExp, makeToken } from '../../test/makeToken'
import type { Session } from '../../types/auth'
import { clearSession, readSession, saveSession } from '../sessionStore'

const session = (exp: number): Session => ({
  token: makeToken({ exp }),
  email: 'stock@erp.local',
  role: 'STOCK',
})

describe('sessionStore', () => {
  afterEach(() => clearSession())

  it('shouldPersistInLocalStorage_whenRemembered', () => {
    const value = session(futureExp())

    saveSession(value, true)

    expect(localStorage.getItem('eclipse.session')).not.toBeNull()
    expect(readSession()).toEqual(value)
  })

  it('shouldPersistInSessionStorage_whenNotRemembered', () => {
    saveSession(session(futureExp()), false)

    expect(localStorage.getItem('eclipse.session')).toBeNull()
    expect(sessionStorage.getItem('eclipse.session')).not.toBeNull()
  })

  it('shouldReturnNullAndClear_whenTokenExpired', () => {
    saveSession(session(1), true)

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
