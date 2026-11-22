import { useEffect } from 'react'
import type { VoicePhase } from '../types/voice'

const ACTIVE_PHASES: VoicePhase[] = ['requesting', 'listening', 'transcribing']

export const useVoiceShortcut = (toggle: () => void, cancel: () => void, phase: VoicePhase, enabled: boolean) => {
  useEffect(() => {
    if (!enabled) return
    const onKeyDown = (event: KeyboardEvent) => {
      const toggling = event.altKey && !event.ctrlKey && !event.metaKey && event.code === 'KeyM'
      if (toggling) {
        event.preventDefault()
        toggle()
      } else if (event.key === 'Escape' && ACTIVE_PHASES.includes(phase)) {
        cancel()
      }
    }
    window.addEventListener('keydown', onKeyDown)
    return () => window.removeEventListener('keydown', onKeyDown)
  }, [toggle, cancel, phase, enabled])
}
