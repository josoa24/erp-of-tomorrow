import {
  MAX_RECORDING_MS,
  MIN_SPEECH_MS,
  NO_SPEECH_TIMEOUT_MS,
  SPEECH_LEVEL,
  TRAILING_SILENCE_MS,
} from '../constants/voiceConfig'

export interface SilenceState {
  elapsedMs: number
  speechMs: number
  silenceMs: number
}

export type SilenceDecision = 'continue' | 'stopAfterSpeech' | 'stopWithoutSpeech' | 'stopAtMaxDuration'

export const INITIAL_SILENCE: SilenceState = { elapsedMs: 0, speechMs: 0, silenceMs: 0 }

export const advanceSilence = (state: SilenceState, level: number, deltaMs: number): SilenceState => {
  const speaking = level >= SPEECH_LEVEL
  return {
    elapsedMs: state.elapsedMs + deltaMs,
    speechMs: speaking ? state.speechMs + deltaMs : state.speechMs,
    silenceMs: speaking ? 0 : state.silenceMs + deltaMs,
  }
}

export const decideSilence = (state: SilenceState): SilenceDecision => {
  if (state.elapsedMs >= MAX_RECORDING_MS) return 'stopAtMaxDuration'
  const heardSpeech = state.speechMs >= MIN_SPEECH_MS
  if (heardSpeech && state.silenceMs >= TRAILING_SILENCE_MS) return 'stopAfterSpeech'
  if (!heardSpeech && state.elapsedMs >= NO_SPEECH_TIMEOUT_MS) return 'stopWithoutSpeech'
  return 'continue'
}
