import { Mic } from 'lucide-react'
import { Spinner } from '../../../../components/ui/spinner'
import { VOICE_SHORTCUT_LABEL } from '../constants/voiceConfig'
import type { VoicePhase } from '../types/voice'
import { voiceButtonVariants } from './voice-button-variants'

interface VoiceButtonProps {
  variant: 'night' | 'light'
  phase: VoicePhase
  level: number
  disabled: boolean
  onToggle: () => void
}

const labelOf = (phase: VoicePhase): string => {
  if (phase === 'listening') return 'Terminer l’enregistrement'
  if (phase === 'requesting' || phase === 'transcribing') return 'Annuler la saisie vocale'
  return `Parler à Eclipse (${VOICE_SHORTCUT_LABEL})`
}

export const VoiceButton = ({ variant, phase, level, disabled, onToggle }: VoiceButtonProps) => {
  const listening = phase === 'listening'
  const busy = phase === 'requesting' || phase === 'transcribing'

  return (
    <button
      type="button"
      aria-label={labelOf(phase)}
      aria-pressed={listening}
      disabled={disabled && phase === 'idle'}
      onClick={onToggle}
      className={voiceButtonVariants({ variant, active: listening })}
    >
      {listening && (
        <span
          aria-hidden="true"
          className="absolute inset-0 rounded-pill bg-status-critical-fg/25 motion-reduce:hidden"
          style={{ transform: `scale(${1 + level * 0.5})` }}
        />
      )}
      {busy ? <Spinner /> : <Mic aria-hidden="true" className="relative" />}
    </button>
  )
}
