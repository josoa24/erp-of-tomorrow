import { ArrowUp, Square } from 'lucide-react'
import { eclipseSendVariants } from './eclipse-bar-variants'

interface EclipseSendButtonProps {
  variant: 'night' | 'light'
  streaming: boolean
  disabled: boolean
  onSend: () => void
  onStop?: () => void
}

export const EclipseSendButton = ({ variant, streaming, disabled, onSend, onStop }: EclipseSendButtonProps) => (
  <button
    type="button"
    disabled={disabled}
    aria-label={streaming ? 'Arrêter la réponse' : 'Envoyer à Eclipse'}
    onClick={streaming ? onStop : onSend}
    className={eclipseSendVariants({ variant })}
  >
    {streaming ? <Square aria-hidden="true" className="fill-current" /> : <ArrowUp aria-hidden="true" />}
  </button>
)
