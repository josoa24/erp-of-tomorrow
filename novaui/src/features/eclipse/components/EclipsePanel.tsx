import { RotateCcw, X } from 'lucide-react'
import { useEffect, useRef } from 'react'
import { Button } from '../../../components/ui/button'
import { IconButton } from '../../../components/ui/icon-button'
import { UNAVAILABLE_VIEWS } from '../services/unavailableView'
import type { EclipseUnavailability } from '../services/eclipseAvailability'
import type { AgentChatState } from '../types/agent'
import { AssistantMessage } from './AssistantMessage'
import { AssistantNotice } from './AssistantNotice'
import { EclipseAvatar } from './EclipseAvatar'

export type PanelPlacement = 'above' | 'below'

interface EclipsePanelProps {
  chat: AgentChatState
  notice: string | null
  unavailability: EclipseUnavailability | null
  placement: PanelPlacement
  onConfirm: (actionId: string) => void
  onCancel: (actionId: string) => void
  onRetry: (summary: string) => void
  onReset: () => void
  onClose: () => void
}

const PLACEMENT: Record<PanelPlacement, string> = { above: 'bottom-full mb-3', below: 'top-full mt-3' }

export const EclipsePanel = ({ chat, notice, unavailability, placement, onConfirm, onCancel, onRetry, onReset, onClose }: EclipsePanelProps) => {
  const endRef = useRef<HTMLDivElement>(null)

  useEffect(() => {
    endRef.current?.scrollIntoView?.({ block: 'end' })
  }, [chat.messages, chat.actions, notice, unavailability])

  return (
    <section
      aria-label="Conversation avec Eclipse"
      className={`absolute inset-x-0 z-30 flex max-h-[min(60vh,560px)] flex-col overflow-hidden rounded-xl border border-line-subtle bg-surface-solid shadow-elevation-2 ${PLACEMENT[placement]}`}
    >
      <header className="flex items-center justify-between gap-2 border-b border-line-subtle px-4 py-2">
        <h2 className="text-label text-ink-primary">Eclipse</h2>
        <div className="flex items-center gap-1">
          <Button size="s" variant="ghost" leadingIcon={<RotateCcw />} onClick={onReset}>Nouvelle conversation</Button>
          <IconButton variant="ghost" size="m" aria-label="Fermer la conversation" onClick={onClose}><X /></IconButton>
        </div>
      </header>
      <div role="log" aria-live="polite" className="flex flex-1 flex-col gap-4 overflow-y-auto p-4">
        {chat.messages.map((message) =>
          message.role === 'user' ? (
            <p key={message.id} className="max-w-[85%] self-end rounded-lg bg-surface-night px-4 py-2 text-body text-ink-inverse">{message.text}</p>
          ) : (
            <AssistantMessage key={message.id} message={message} actions={chat.actions} onConfirm={onConfirm} onCancel={onCancel} onRetry={onRetry} />
          ),
        )}
        {notice && (
          <div className="flex items-start gap-3">
            <EclipseAvatar />
            <p className="text-body text-ink-primary">{notice}</p>
          </div>
        )}
        {unavailability && <AssistantNotice tone="error" {...UNAVAILABLE_VIEWS[unavailability]} />}
        <div ref={endRef} />
      </div>
    </section>
  )
}
