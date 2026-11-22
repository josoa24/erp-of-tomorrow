import { OctagonAlert, TriangleAlert, Info, type LucideIcon } from 'lucide-react'
import { Panel } from '../../../../components/ui/panel'
import type { UiAlertItem, UiTone } from '../../types/uiBlock'

const ICONS: Record<UiTone, LucideIcon> = {
  critical: OctagonAlert,
  warning: TriangleAlert,
  success: Info,
  info: Info,
  neutral: Info,
}

const TONE_CLASSES: Record<UiTone, string> = {
  critical: 'bg-status-critical-bg text-status-critical-fg',
  warning: 'bg-status-warning-bg text-status-warning-fg',
  success: 'bg-status-success-bg text-status-success-fg',
  info: 'bg-status-info-bg text-status-info-fg',
  neutral: 'bg-surface-sunken text-ink-secondary',
}

export const AlertsBlock = ({ title, items }: { title: string; items: UiAlertItem[] }) => (
  <Panel aria-label={title} padding="m" className="flex flex-col gap-2 bg-surface-solid">
    <h4 className="text-label text-ink-secondary">{title}</h4>
    <ul className="flex flex-col gap-2">
      {items.map((item) => {
        const Icon = ICONS[item.tone]
        return (
          <li key={item.title} className={`flex items-start gap-3 rounded-md px-3 py-2 ${TONE_CLASSES[item.tone]}`}>
            <Icon aria-hidden="true" className="mt-0.5 size-5 shrink-0" />
            <span className="flex flex-col">
              <span className="text-label">{item.title}</span>
              <span className="text-micro">{item.detail}</span>
            </span>
          </li>
        )
      })}
    </ul>
  </Panel>
)
