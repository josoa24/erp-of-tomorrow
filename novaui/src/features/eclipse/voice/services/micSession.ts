import { LEVEL_INTERVAL_MS } from '../constants/voiceConfig'
import type { RecordingReason, RecordingResult } from '../types/voice'
import { rmsLevel } from './voiceLevel'
import { INITIAL_SILENCE, advanceSilence, decideSilence } from './voiceSilence'
import { pickMimeType } from './voiceSupport'

export interface MicSession {
  stop: () => void
  cancel: () => void
}

interface SessionHandlers {
  onLevel: (level: number) => void
  onResult: (result: RecordingResult) => void
}

type Outcome = RecordingReason | 'cancelled' | 'silent'

const DECISIONS: Record<string, Outcome> = {
  stopAfterSpeech: 'silence',
  stopAtMaxDuration: 'maxDuration',
  stopWithoutSpeech: 'silent',
}

export const openMicSession = async ({ onLevel, onResult }: SessionHandlers): Promise<MicSession> => {
  const stream = await navigator.mediaDevices.getUserMedia({ audio: { echoCancellation: true, noiseSuppression: true } })
  const context = new AudioContext()
  try {
    await context.resume()
  } catch {
    stream.getTracks().forEach((track) => track.stop())
    throw new DOMException('Audio indisponible', 'NotReadableError')
  }
  const analyser = context.createAnalyser()
  analyser.fftSize = 1024
  context.createMediaStreamSource(stream).connect(analyser)
  const mimeType = pickMimeType()
  const recorder = new MediaRecorder(stream, mimeType ? { mimeType } : undefined)
  const chunks: Blob[] = []
  const samples = new Uint8Array(analyser.fftSize)
  let silence = INITIAL_SILENCE
  let outcome: Outcome = 'manual'
  let finished = false

  const release = () => {
    clearInterval(timer)
    stream.getTracks().forEach((track) => track.stop())
    void context.close()
  }

  const end = (reason: Outcome) => {
    if (finished) return
    finished = true
    outcome = reason
    if (recorder.state === 'inactive') release()
    else recorder.stop()
  }

  recorder.ondataavailable = (event) => {
    if (event.data.size > 0) chunks.push(event.data)
  }
  recorder.onstop = () => {
    release()
    if (outcome === 'cancelled') return
    if (outcome === 'silent') return onResult({ silent: true })
    onResult({ blob: new Blob(chunks, { type: recorder.mimeType || mimeType || 'audio/webm' }), reason: outcome })
  }

  const timer = setInterval(() => {
    analyser.getByteTimeDomainData(samples)
    const level = rmsLevel(samples)
    onLevel(level)
    silence = advanceSilence(silence, level, LEVEL_INTERVAL_MS)
    const next = DECISIONS[decideSilence(silence)]
    if (next) end(next)
  }, LEVEL_INTERVAL_MS)

  recorder.start(250)
  return { stop: () => end('manual'), cancel: () => end('cancelled') }
}
