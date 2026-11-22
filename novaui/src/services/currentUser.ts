import { ROLE_LABELS } from '../constants/roleLabels'
import type { Session } from '../types/auth'
import type { CurrentUser } from '../types/currentUser'
import { toInitials } from '../utils/initials'

const capitalize = (word: string): string =>
  word.charAt(0).toLocaleUpperCase('fr-FR') + word.slice(1).toLocaleLowerCase('fr-FR')

export const displayNameFromEmail = (email: string): string => {
  const localPart = email.split('@')[0] ?? ''
  const words = localPart.split(/[._-]+/).filter(Boolean)
  return words.length > 0 ? words.map(capitalize).join(' ') : email
}

export const toCurrentUser = ({ email, role, fullName }: Pick<Session, 'email' | 'role' | 'fullName'>): CurrentUser => {
  const displayName = fullName?.trim() || displayNameFromEmail(email)
  return { email, role, roleLabel: ROLE_LABELS[role], displayName, initials: toInitials(displayName) }
}
