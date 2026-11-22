import { ROLE_LABELS } from '../constants/roleLabels'
import type { Session } from '../types/auth'
import type { CurrentUser } from '../types/currentUser'
import { sessionRoles } from '../utils/roleAccess'
import { toInitials } from '../utils/initials'

const capitalize = (word: string): string =>
  word.charAt(0).toLocaleUpperCase('fr-FR') + word.slice(1).toLocaleLowerCase('fr-FR')

export const displayNameFromEmail = (email: string): string => {
  const localPart = email.split('@')[0] ?? ''
  const words = localPart.split(/[._-]+/).filter(Boolean)
  return words.length > 0 ? words.map(capitalize).join(' ') : email
}

type SessionIdentity = Pick<Session, 'email' | 'role' | 'roles' | 'fullName'>

export const toCurrentUser = ({ email, role, roles, fullName }: SessionIdentity): CurrentUser => {
  const displayName = fullName?.trim() || displayNameFromEmail(email)
  const all = sessionRoles({ role, roles })
  const roleLabels = all.map((item) => ROLE_LABELS[item])
  return { email, role, roles: all, roleLabel: roleLabels.join(', '), roleLabels, displayName, initials: toInitials(displayName) }
}
