import { LogIn, SearchX, ServerCrash, ShieldAlert, TriangleAlert, WifiOff, type LucideIcon } from 'lucide-react'
import { ROUTES } from '../../navigation/routes'
import type { ErrorRouteState } from '../../types/errorRoute'

export type ErrorKind =
  | 'loginRefused'
  | 'sessionExpired'
  | 'forbidden'
  | 'notFound'
  | 'validation'
  | 'business'
  | 'unavailable'
  | 'server'

export type ErrorAction = 'login' | 'retry' | 'back'

export interface ErrorDisplay {
  title: string
  icon: LucideIcon
  action: ErrorAction
}

const UNAVAILABLE_STATUSES = [502, 503, 504]

export const DISPLAYS: Record<ErrorKind, ErrorDisplay> = {
  loginRefused: { title: 'Connexion refusée', icon: LogIn, action: 'login' },
  sessionExpired: { title: 'Session expirée', icon: LogIn, action: 'login' },
  forbidden: { title: 'Accès refusé', icon: ShieldAlert, action: 'back' },
  notFound: { title: 'Introuvable', icon: SearchX, action: 'back' },
  validation: { title: 'Données invalides', icon: TriangleAlert, action: 'back' },
  business: { title: 'Opération impossible', icon: TriangleAlert, action: 'back' },
  unavailable: { title: 'Service indisponible', icon: WifiOff, action: 'retry' },
  server: { title: 'Erreur serveur', icon: ServerCrash, action: 'retry' },
}

export const ACTION_LABELS: Record<ErrorAction, string> = {
  login: 'Revenir à la connexion',
  retry: 'Réessayer',
  back: 'Retour',
}

export const errorKindOf = (error: ErrorRouteState | undefined): ErrorKind => {
  if (!error) return 'notFound'
  if (error.type === 'unauthorized') return error.from === ROUTES.login ? 'loginRefused' : 'sessionExpired'
  if (error.type === 'network') return 'unavailable'
  if (error.type === 'server') {
    return error.status !== undefined && UNAVAILABLE_STATUSES.includes(error.status) ? 'unavailable' : 'server'
  }
  return error.type
}

export const errorChip = (error: ErrorRouteState | undefined): string | null => {
  const parts = [error?.code, error?.status].filter((part) => part !== undefined && part !== '')
  return parts.length > 0 ? parts.join(' · ') : null
}
