import { Sparkles } from 'lucide-react'
import { useRef } from 'react'
import type { RefObject } from 'react'
import { useFocusShortcut } from '@/hooks/useFocusShortcut'
import { cn } from '@/lib/utils'
import { modKeyLabel } from '@/utils/shortcut'
import barLinesUrl from '../../assets/eclipse-bar-lines.svg'
import { Kbd } from '../ui/kbd'
import { eclipseBarVariants, eclipseOrbVariants } from './eclipse-bar-variants'
import { EclipseSendButton } from './EclipseSendButton'
import { useEclipseBarKeys } from './useEclipseBarKeys'

export type EclipseBarStatus = 'idle' | 'streaming' | 'unavailable'

export interface EclipseBarProps {
  variant?: 'night' | 'light'
  placeholder: string
  value: string
  onChange: (value: string) => void
  onSubmit: (text: string) => void
  status?: EclipseBarStatus
  onStop?: () => void
  unavailableLabel?: string
  label?: string
  shortcut?: boolean
  inputRef?: RefObject<HTMLTextAreaElement | null>
  onBlur?: () => void
  className?: string
}

export const EclipseBar = ({
  variant = 'light',
  placeholder,
  value,
  onChange,
  onSubmit,
  status = 'idle',
  onStop,
  unavailableLabel = 'Eclipse est indisponible',
  label = 'Demander à Eclipse',
  shortcut = true,
  inputRef,
  onBlur,
  className,
}: EclipseBarProps) => {
  const ownRef = useRef<HTMLTextAreaElement>(null)
  const textareaRef = inputRef ?? ownRef
  const unavailable = status === 'unavailable'
  useFocusShortcut(textareaRef, 'mod-k', shortcut && !unavailable)
  const submit = () => {
    const text = value.trim()
    if (text && status === 'idle') onSubmit(text)
  }
  const onKeyDown = useEclipseBarKeys(submit)

  return (
    <div className={cn(eclipseBarVariants({ variant, unavailable }), className)}>
      {variant === 'night' && !unavailable && (
        <img src={barLinesUrl} alt="" aria-hidden="true" className="pointer-events-none absolute top-0 left-0 h-16 w-[640px] max-w-none" />
      )}
      <span aria-hidden="true" className={eclipseOrbVariants({ variant })}>
        <Sparkles />
      </span>
      <textarea
        ref={textareaRef}
        rows={1}
        value={value}
        disabled={unavailable}
        aria-label={label}
        placeholder={unavailable ? unavailableLabel : placeholder}
        onChange={(event) => onChange(event.target.value)}
        onKeyDown={onKeyDown}
        onBlur={onBlur}
        className={cn(
          'relative max-h-12 min-w-0 flex-1 resize-none overflow-y-auto bg-transparent text-body outline-none [field-sizing:content]',
          variant === 'night' ? 'placeholder:text-ink-inverse/80' : 'placeholder:text-ink-tertiary',
        )}
      />
      {!unavailable && (
        <Kbd inverse={variant === 'night'} aria-hidden="true" className="relative max-sm:hidden">
          {modKeyLabel()}
        </Kbd>
      )}
      <EclipseSendButton variant={variant} streaming={status === 'streaming'} disabled={unavailable} onSend={submit} onStop={onStop} />
    </div>
  )
}
