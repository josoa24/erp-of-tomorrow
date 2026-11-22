export const VOICE_LANGUAGE = 'fr'
export const TRANSCRIBE_PATH = '/api/voice/transcribe'
export const TRANSCRIBE_TIMEOUT_MS = 90_000
export const MAX_AUDIO_BYTES = 10 * 1024 * 1024
export const MAX_RECORDING_MS = 45_000
export const NO_SPEECH_TIMEOUT_MS = 7_000
export const TRAILING_SILENCE_MS = 1_400
export const MIN_SPEECH_MS = 250
export const SPEECH_LEVEL = 0.12
export const LEVEL_INTERVAL_MS = 80
export const TRANSCRIBED_NOTICE_MS = 6_000
export const AUTO_SEND_KEY = 'eclipse.voice.autoSend'
export const VOICE_SHORTCUT_LABEL = 'Alt M'
export const MIME_CANDIDATES = ['audio/webm;codecs=opus', 'audio/webm', 'audio/ogg;codecs=opus', 'audio/mp4'] as const
