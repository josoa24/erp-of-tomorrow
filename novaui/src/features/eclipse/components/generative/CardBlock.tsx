import { Panel } from '../../../../components/ui/panel'
import { StatusPill } from '../../../../components/ui/status-pill'
import { formatCell } from '../../services/blockFormat'
import type { UiField, UiTone } from '../../types/uiBlock'

interface CardBlockProps {
  title: string
  subtitle: string
  tone: UiTone
  fields: UiField[]
}

export const CardBlock = ({ title, subtitle, tone, fields }: CardBlockProps) => (
  <Panel aria-label={title} padding="m" className="flex flex-col gap-3 bg-surface-solid">
    <div className="flex flex-wrap items-center justify-between gap-2">
      <h4 className="text-body-strong text-ink-primary">{title}</h4>
      {subtitle && <StatusPill label={subtitle} tone={tone} />}
    </div>
    <dl className="grid grid-cols-2 gap-3 sm:grid-cols-3">
      {fields.map((field) => (
        <div key={field.label} className="flex flex-col">
          <dt className="text-caption text-ink-secondary uppercase">{field.label}</dt>
          <dd className="text-body text-ink-primary">{formatCell(field.value, typeof field.value === 'number' ? 'number' : 'text')}</dd>
        </div>
      ))}
    </dl>
  </Panel>
)
