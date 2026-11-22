import type { EclipseUnavailability } from './eclipseAvailability'

export interface UnavailableView {
  title: string
  detail: string
  code: string
}

export const UNAVAILABLE_VIEWS: Record<EclipseUnavailability, UnavailableView> = {
  rateLimited: {
    title: 'Eclipse est très sollicité',
    detail: 'Le quota de l’IA est atteint. Votre question est conservée : réessayez dans une minute.',
    code: 'GEMINI_RATE_LIMIT · 429',
  },
  unavailable: {
    title: 'Eclipse est indisponible',
    detail: 'Le service IA ne répond pas pour le moment. Le reste de l’ERP fonctionne.',
    code: 'SERVER · 503',
  },
}
