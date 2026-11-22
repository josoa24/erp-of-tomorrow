import type { VoiceFailure, VoicePhase } from '../types/voice'

export const statusText = (phase: VoicePhase, failure: VoiceFailure | null): string => {
  if (phase === 'requesting') return 'Autorisez l’accès au micro dans votre navigateur…'
  if (phase === 'listening') return 'Je vous écoute… faites une pause pour terminer.'
  if (phase === 'transcribing') return 'Transcription en cours…'
  if (phase === 'transcribed') return 'Texte transcrit. Vérifiez-le, puis envoyez-le.'
  return failure?.message ?? ''
}
