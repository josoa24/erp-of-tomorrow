import { useCallback, useEffect, useRef, useState } from 'react'
import { readAutoSend, writeAutoSend } from '../services/voiceAutoSend'

export const useAutoSend = () => {
  const [autoSend, setAutoSend] = useState(readAutoSend)
  const autoSendRef = useRef(autoSend)

  useEffect(() => {
    autoSendRef.current = autoSend
  }, [autoSend])

  const change = useCallback((enabled: boolean) => {
    setAutoSend(enabled)
    writeAutoSend(enabled)
  }, [])

  return { autoSend, autoSendRef, change }
}
