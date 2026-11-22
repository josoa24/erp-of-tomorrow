import type { ErrorType } from '../types/apiError'

interface ErrorContent {
  title: string
  message: string
}

export const ERROR_CONTENT: Record<ErrorType, ErrorContent> = {
  network: { title: 'Connexion impossible', message: 'Le serveur est injoignable.' },
  notFound: { title: 'Page introuvable', message: "Cette page n'existe pas ou a été déplacée." },
  validation: { title: 'Données invalides', message: 'Certaines informations sont incorrectes.' },
  forbidden: { title: 'Accès refusé', message: "Votre rôle ne donne pas accès à ce module." },
  server: { title: 'Erreur du serveur', message: 'Une erreur est survenue, réessayez plus tard.' },
  business: { title: 'Opération impossible', message: "Cette opération n'est pas autorisée." },
  unauthorized: { title: 'Connexion refusée', message: 'Veuillez vous reconnecter.' },
}
