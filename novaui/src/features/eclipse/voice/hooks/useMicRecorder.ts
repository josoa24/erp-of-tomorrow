import { useCallback, useEffect, useRef, useState } from 'react'
import { openMicSession, type MicSession } from '../services/micSession'
import type { RecordingResult } from '../types/voice'

export type RecorderPhase = 'idle' | 'requesting' | 'recording'

interface RecorderHandlers {
  onLevel: (level: number) => void
  onResult: (result: RecordingResult) => void
  onError: (error: unknown) => void
}

export const useMicRecorder = (handlers: RecorderHandlers) => {
  const [phase, setPhase] = useState<RecorderPhase>('idle')
  const sessionRef = useRef<MicSession | null>(null)
  const requestRef = useRef(0)
  const handlersRef = useRef(handlers)

  useEffect(() => {
    handlersRef.current = handlers
  })

  const start = useCallback(async () => {
    if (sessionRef.current) return
    const request = ++requestRef.current
    setPhase('requesting')
    try {
      const session = await openMicSession({
        onLevel: (level) => handlersRef.current.onLevel(level),
        onResult: (result) => {
          sessionRef.current = null
          setPhase('idle')
          handlersRef.current.onResult(result)
        },
      })
      if (request !== requestRef.current) {
        session.cancel()
        return
      }
      sessionRef.current = session
      setPhase('recording')
    } catch (error) {
      if (request !== requestRef.current) return
      setPhase('idle')
      handlersRef.current.onError(error)
    }
  }, [])

  const stop = useCallback(() => sessionRef.current?.stop(), [])

  const cancel = useCallback(() => {
    requestRef.current += 1
    sessionRef.current?.cancel()
    sessionRef.current = null
    setPhase('idle')
  }, [])

  useEffect(
    () => () => {
      requestRef.current += 1
      sessionRef.current?.cancel()
    },
    [],
  )

  return { phase, start, stop, cancel }
}
