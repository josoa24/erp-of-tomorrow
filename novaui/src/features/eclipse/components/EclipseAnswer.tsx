import { Button } from '../../../components/ui/button'
import { Panel } from '../../../components/ui/panel'

interface EclipseAnswerProps {
  text: string
  onDismiss: () => void
}

export const EclipseAnswer = ({ text, onDismiss }: EclipseAnswerProps) => (
  <Panel role="status" padding="l" className="mb-2 flex items-start justify-between gap-3">
    <p className="text-body text-ink-primary">{text}</p>
    <Button size="s" variant="ghost" onClick={onDismiss}>Fermer</Button>
  </Panel>
)
