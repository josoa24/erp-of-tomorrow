export type VoicePhase = 'idle' | 'requesting' | 'listening' | 'transcribing' | 'transcribed' | 'failed'

export type VoiceFailureKind =
  | 'insecure'
  | 'unsupported'
  | 'permissionDenied'
  | 'noDevice'
  | 'micBusy'
  | 'silence'
  | 'tooLong'
  | 'tooLarge'
  | 'unreadable'
  | 'unsupportedFormat'
  | 'unavailable'
  | 'unknown'

export interface VoiceFailure {
  kind: VoiceFailureKind
  message: string
  retryable: boolean
}

export interface TranscriptionResult {
  text: string
  language: string
  durationSeconds: number
}

export type RecordingReason = 'manual' | 'silence' | 'maxDuration'

export type RecordingResult = { blob: Blob; reason: RecordingReason } | { silent: true }
