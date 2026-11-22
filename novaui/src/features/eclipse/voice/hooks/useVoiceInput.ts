import { useCallback, useEffect, useMemo, useRef, useState } from 'react'
import { useApiError } from '../../../../hooks/useApiError'
import { transcribeAudio } from '../api/voiceApi'
import { MAX_AUDIO_BYTES, TRANSCRIBED_NOTICE_MS, VOICE_LANGUAGE } from '../constants/voiceConfig'
import { failureOf, failureOfApiError, failureOfDeviceError } from '../services/voiceFailure'
import { checkVoiceSupport } from '../services/voiceSupport'
import type { RecordingResult, VoiceFailure, VoicePhase } from '../types/voice'
import { useAutoSend } from './useAutoSend'
import { useMicRecorder } from './useMicRecorder'

type LocalPhase = 'idle' | 'transcribing' | 'transcribed' | 'failed'

export const useVoiceInput = (onTranscript: (text: string, send: boolean) => void) => {
  const handleError = useApiError()
  const support = useMemo(checkVoiceSupport, [])
  const { autoSend, autoSendRef, change } = useAutoSend()
  const [local, setLocal] = useState<LocalPhase>('idle')
  const [failure, setFailure] = useState<VoiceFailure | null>(null)
  const [level, setLevel] = useState(0)
  const controllerRef = useRef<AbortController | null>(null)

  const fail = useCallback((next: VoiceFailure) => {
    setFailure(next)
    setLocal('failed')
  }, [])

  const transcribe = useCallback(
    async (blob: Blob) => {
      const controller = new AbortController()
      controllerRef.current = controller
      setLocal('transcribing')
      try {
        const text = (await transcribeAudio(blob, VOICE_LANGUAGE, controller.signal)).text.trim()
        if (!text) return fail(failureOf('silence'))
        onTranscript(text, autoSendRef.current)
        setLocal(autoSendRef.current ? 'idle' : 'transcribed')
      } catch (error) {
        if (controller.signal.aborted) return
        const known = failureOfApiError(error)
        if (known) fail(known)
        else handleError(error)
      } finally {
        if (controllerRef.current === controller) controllerRef.current = null
      }
    },
    [autoSendRef, fail, handleError, onTranscript],
  )

  const recorder = useMicRecorder({
    onLevel: setLevel,
    onResult: (result: RecordingResult) => {
      setLevel(0)
      if ('silent' in result) return fail(failureOf('silence'))
      if (result.blob.size > MAX_AUDIO_BYTES) return fail(failureOf('tooLong'))
      void transcribe(result.blob)
    },
    onError: (error) => fail(failureOfDeviceError(error)),
  })

  const start = useCallback(() => {
    if (support) return fail(support)
    setFailure(null)
    setLocal('idle')
    void recorder.start()
  }, [fail, recorder, support])

  const cancel = useCallback(() => {
    controllerRef.current?.abort()
    controllerRef.current = null
    recorder.cancel()
    setLevel(0)
    setLocal('idle')
  }, [recorder])

  const phase: VoicePhase =
    recorder.phase === 'requesting' ? 'requesting' : recorder.phase === 'recording' ? 'listening' : local

  const toggle = useCallback(() => {
    if (phase === 'listening') recorder.stop()
    else if (phase === 'requesting' || phase === 'transcribing') cancel()
    else start()
  }, [cancel, phase, recorder, start])

  useEffect(() => {
    if (local !== 'transcribed') return
    const timer = setTimeout(() => setLocal('idle'), TRANSCRIBED_NOTICE_MS)
    return () => clearTimeout(timer)
  }, [local])

  const dismiss = useCallback(() => setLocal('idle'), [])

  return { phase, failure, level, autoSend, changeAutoSend: change, toggle, start, cancel, dismiss }
}
