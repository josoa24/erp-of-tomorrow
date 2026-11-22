import { ApiError } from '../../../../types/apiError'
import type { VoiceFailure, VoiceFailureKind } from '../types/voice'

const FAILURES: Record<VoiceFailureKind, { message: string; retryable: boolean }> = {
  insecure: { message: 'Le navigateur n’autorise le micro qu’en HTTPS ou sur localhost.', retryable: false },
  unsupported: {
    message: 'Ce navigateur ne permet pas l’enregistrement vocal. Essayez Chrome, Edge ou Firefox.',
    retryable: false,
  },
  permissionDenied: {
    message: 'L’accès au micro est refusé. Autorisez-le dans les réglages du navigateur, puis réessayez.',
    retryable: true,
  },
  noDevice: { message: 'Aucun micro détecté. Branchez un micro puis réessayez.', retryable: true },
  micBusy: { message: 'Le micro est utilisé par une autre application. Fermez-la puis réessayez.', retryable: true },
  silence: { message: 'Je n’ai rien entendu. Rapprochez-vous du micro et réessayez.', retryable: true },
  tooLong: { message: 'L’enregistrement est trop long. Faites une phrase plus courte.', retryable: true },
  tooLarge: {
    message: 'L’enregistrement dépasse la taille autorisée (10 Mo). Faites une phrase plus courte.',
    retryable: true,
  },
  unreadable: { message: 'Je n’ai pas pu lire cet enregistrement. Réessayez.', retryable: true },
  unsupportedFormat: {
    message: 'Le format audio de ce navigateur n’est pas accepté par le service vocal.',
    retryable: false,
  },
  unavailable: { message: 'Le service vocal est indisponible pour le moment. Écrivez votre demande.', retryable: true },
  unknown: { message: 'La transcription a échoué. Réessayez.', retryable: true },
}

const DEVICE_ERRORS: Record<string, VoiceFailureKind> = {
  NotAllowedError: 'permissionDenied',
  SecurityError: 'permissionDenied',
  NotFoundError: 'noDevice',
  OverconstrainedError: 'noDevice',
  NotReadableError: 'micBusy',
  AbortError: 'micBusy',
}

const STATUS_FAILURES: Record<number, VoiceFailureKind> = {
  400: 'silence',
  413: 'tooLarge',
  415: 'unsupportedFormat',
  422: 'unreadable',
  502: 'unavailable',
  503: 'unavailable',
  504: 'unavailable',
}

export const failureOf = (kind: VoiceFailureKind): VoiceFailure => ({ kind, ...FAILURES[kind] })

export const failureOfDeviceError = (error: unknown): VoiceFailure =>
  failureOf(error instanceof DOMException ? (DEVICE_ERRORS[error.name] ?? 'unknown') : 'unknown')

export const failureOfApiError = (error: unknown): VoiceFailure | null => {
  if (!(error instanceof ApiError)) return failureOf('unknown')
  if (error.type === 'network') return failureOf('unavailable')
  const kind = STATUS_FAILURES[error.status]
  return kind ? failureOf(kind) : null
}
