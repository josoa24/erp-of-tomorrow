import { MIME_CANDIDATES } from '../constants/voiceConfig'
import type { VoiceFailure } from '../types/voice'
import { failureOf } from './voiceFailure'

export const checkVoiceSupport = (): VoiceFailure | null => {
  if (typeof window === 'undefined' || !window.isSecureContext) return failureOf('insecure')
  const capable = Boolean(navigator.mediaDevices?.getUserMedia) && typeof MediaRecorder !== 'undefined'
  return capable ? null : failureOf('unsupported')
}

export const pickMimeType = (): string | undefined => MIME_CANDIDATES.find((type) => MediaRecorder.isTypeSupported(type))

export const fileNameFor = (mimeType: string): string => {
  const base = mimeType.split(';')[0]?.trim().toLowerCase()
  if (base === 'audio/mp4') return 'voix.m4a'
  if (base === 'audio/ogg') return 'voix.ogg'
  return 'voix.webm'
}
