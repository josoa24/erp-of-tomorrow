import { toCurrentUser } from '../services/currentUser'
import { readSession } from '../services/sessionStore'
import type { CurrentUser } from '../types/currentUser'

export const useCurrentUser = (): CurrentUser | null => {
  const session = readSession()
  return session ? toCurrentUser(session) : null
}
