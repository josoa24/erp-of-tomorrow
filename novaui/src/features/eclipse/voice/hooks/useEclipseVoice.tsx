import { VoiceButton } from '../components/VoiceButton'
import { VoiceStatus } from '../components/VoiceStatus'
import { useVoiceInput } from './useVoiceInput'
import { useVoiceShortcut } from './useVoiceShortcut'

interface EclipseVoiceOptions {
  variant: 'night' | 'light'
  disabled: boolean
  onText: (text: string, send: boolean) => void
}

export const useEclipseVoice = ({ variant, disabled, onText }: EclipseVoiceOptions) => {
  const voice = useVoiceInput(onText)
  useVoiceShortcut(voice.toggle, voice.cancel, voice.phase, !disabled || voice.phase !== 'idle')

  return {
    button: (
      <VoiceButton
        variant={variant}
        phase={voice.phase}
        level={voice.level}
        disabled={disabled}
        onToggle={voice.toggle}
      />
    ),
    status: (
      <VoiceStatus
        phase={voice.phase}
        failure={voice.failure}
        level={voice.level}
        autoSend={voice.autoSend}
        onAutoSendChange={voice.changeAutoSend}
        onCancel={voice.cancel}
        onRetry={voice.start}
        onDismiss={voice.dismiss}
      />
    ),
  }
}
