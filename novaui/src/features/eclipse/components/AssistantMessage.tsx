import { progressLabel } from '../services/toolLabels'
import type { ActionEntry, AgentMessage } from '../types/agent'
import { ActionCard } from './ActionCard'
import { AssistantNotice } from './AssistantNotice'
import { AssistantProgress } from './AssistantProgress'
import { EclipseAvatar } from './EclipseAvatar'
import { UiBlockView } from './generative/UiBlockView'
import { MarkdownText } from './MarkdownText'

interface AssistantMessageProps {
  message: AgentMessage
  actions: Record<string, ActionEntry>
  onConfirm: (actionId: string) => void
  onCancel: (actionId: string) => void
  onRetry: (summary: string) => void
}

const isEmpty = (message: AgentMessage): boolean =>
  message.status === 'done' && !message.text.trim() && message.blocks.length === 0 && message.actionIds.length === 0

export const AssistantMessage = ({ message, actions, onConfirm, onCancel, onRetry }: AssistantMessageProps) => {
  const streaming = message.status === 'streaming'
  const showProgress = streaming && !message.text && message.blocks.length === 0
  return (
    <div className="flex items-start gap-3">
      <EclipseAvatar />
      <div className="flex min-w-0 flex-1 flex-col gap-3" aria-busy={streaming || undefined}>
        {showProgress && <AssistantProgress label={progressLabel(message.toolCalls)} />}
        {message.blocks.map((block, index) => (
          <UiBlockView key={index} block={block} />
        ))}
        {message.text && <MarkdownText text={message.text} />}
        {message.actionIds.map((id) => {
          const entry = actions[id]
          return entry ? (
            <ActionCard key={id} entry={entry} onConfirm={() => onConfirm(id)} onCancel={() => onCancel(id)} onRetry={() => onRetry(entry.action.summary)} />
          ) : null
        })}
        {message.status === 'error' && <AssistantNotice tone="error" title="Réponse interrompue" detail={message.error ?? 'Eclipse n’a pas pu répondre.'} />}
        {message.status === 'stopped' && <AssistantNotice tone="info" title="Réponse arrêtée" detail="La partie déjà reçue reste affichée." />}
        {isEmpty(message) && <AssistantNotice tone="info" title="Pas de réponse à afficher" detail="Reformulez votre demande ou précisez ce que vous cherchez." />}
      </div>
    </div>
  )
}
