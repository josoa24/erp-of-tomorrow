import { MicOff } from 'lucide-react'
import { Button } from '../../../../components/ui/button'
import { Spinner } from '../../../../components/ui/spinner'
import { statusText } from '../services/voiceStatusText'
import type { VoiceFailure, VoicePhase } from '../types/voice'
import { VoiceAutoSendSwitch } from './VoiceAutoSendSwitch'
import { VoiceLevelMeter } from './VoiceLevelMeter'

interface VoiceStatusProps {
  phase: VoicePhase
  failure: VoiceFailure | null
  level: number
  autoSend: boolean
  onAutoSendChange: (enabled: boolean) => void
  onCancel: () => void
  onRetry: () => void
  onDismiss: () => void
}

const Lead = ({ phase, level }: { phase: VoicePhase; level: number }) => {
  if (phase === 'listening') return <VoiceLevelMeter level={level} />
  if (phase === 'failed') return <MicOff aria-hidden="true" className="size-4 shrink-0 text-status-critical-fg" />
  if (phase === 'requesting' || phase === 'transcribing') return <Spinner className="size-4" />
  return null
}

export const VoiceStatus = (props: VoiceStatusProps) => {
  const { phase, failure, level, autoSend, onAutoSendChange, onCancel, onRetry, onDismiss } = props
  if (phase === 'idle') return null
  const active = phase === 'requesting' || phase === 'listening' || phase === 'transcribing'

  return (
    <div
      role={phase === 'failed' ? 'alert' : 'status'}
      className="mb-2 flex flex-wrap items-center gap-x-3 gap-y-2 rounded-lg border border-line-subtle bg-surface-solid px-4 py-2.5 text-body-s text-ink-primary shadow-elevation-1"
    >
      <Lead phase={phase} level={level} />
      <span className="min-w-0 flex-1">{statusText(phase, failure)}</span>
      {(phase === 'listening' || phase === 'transcribed') && (
        <VoiceAutoSendSwitch enabled={autoSend} onChange={onAutoSendChange} />
      )}
      {active && (
        <Button size="s" variant="ghost" onClick={onCancel}>
          Annuler
        </Button>
      )}
      {phase === 'failed' && failure?.retryable && (
        <Button size="s" variant="secondary" onClick={onRetry}>
          Réessayer
        </Button>
      )}
      {(phase === 'failed' || phase === 'transcribed') && (
        <Button size="s" variant="ghost" onClick={onDismiss}>
          Fermer
        </Button>
      )}
    </div>
  )
}
