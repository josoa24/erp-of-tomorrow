import { CircleAlert, Info, type LucideIcon } from 'lucide-react'

type NoticeTone = 'error' | 'info'

const STYLES: Record<NoticeTone, { icon: LucideIcon; box: string }> = {
  error: { icon: CircleAlert, box: 'bg-status-critical-bg text-status-critical-fg' },
  info: { icon: Info, box: 'bg-surface-sunken text-ink-secondary' },
}

interface AssistantNoticeProps {
  tone: NoticeTone
  title: string
  detail?: string
  code?: string
}

export const AssistantNotice = ({ tone, title, detail, code }: AssistantNoticeProps) => {
  const { icon: Icon, box } = STYLES[tone]
  return (
    <div role="alert" className={`flex flex-col gap-1 rounded-md px-4 py-3 ${box}`}>
      <p className="flex items-center gap-2 text-body-strong">
        <Icon aria-hidden="true" className="size-4 shrink-0" />
        {title}
      </p>
      {detail && <p className="text-body-s">{detail}</p>}
      {code && <p className="text-micro opacity-80">{code}</p>}
    </div>
  )
}
