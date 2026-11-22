import type { Session } from '../types/auth'
import { sessionRoles } from '../utils/roleAccess'
import { isTokenExpired } from '../utils/jwt'

const SESSION_KEY = 'eclipse.session'

const storages = (): Storage[] => [localStorage, sessionStorage]

const readRaw = (storage: Storage): string | null => {
  try {
    return storage.getItem(SESSION_KEY)
  } catch {
    return null
  }
}

const parseSession = (raw: string): Session | null => {
  try {
    const value = JSON.parse(raw) as Partial<Session>
    if (!value.token || !value.email || !value.role) return null
    return { ...value, roles: sessionRoles(value as Session) } as Session
  } catch {
    return null
  }
}

export const clearSession = (): void => {
  storages().forEach((storage) => {
    try {
      storage.removeItem(SESSION_KEY)
    } catch {
      return
    }
  })
}

export const saveSession = (session: Session): void => {
  clearSession()
  localStorage.setItem(SESSION_KEY, JSON.stringify({ ...session, roles: sessionRoles(session) }))
}

export const readSession = (now = Date.now()): Session | null => {
  for (const storage of storages()) {
    const raw = readRaw(storage)
    if (!raw) continue
    const session = parseSession(raw)
    if (session && !isTokenExpired(session.token, now)) return session
    clearSession()
    return null
  }
  return null
}
