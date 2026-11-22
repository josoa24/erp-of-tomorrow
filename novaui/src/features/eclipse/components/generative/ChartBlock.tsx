import { Panel } from '../../../../components/ui/panel'
import { formatCell } from '../../services/blockFormat'
import type { UiCellKind, UiChartPoint } from '../../types/uiBlock'

const BAR_AREA = 120
const MIN_BAR = 3

interface ChartBlockProps {
  title: string
  kind: UiCellKind
  points: UiChartPoint[]
}

export const ChartBlock = ({ title, kind, points }: ChartBlockProps) => {
  const peak = Math.max(...points.map((point) => point.value), 1)

  return (
    <Panel aria-label={title} padding="m" className="flex flex-col gap-3 bg-surface-solid">
      <h4 className="text-label text-ink-secondary">{title}</h4>
      <ul className="flex items-end gap-2 overflow-x-auto" style={{ height: BAR_AREA + 48 }}>
        {points.map((point) => (
          <li key={point.label} className="flex min-w-10 flex-1 flex-col items-center justify-end gap-1" title={`${point.label} : ${formatCell(point.value, kind)}`}>
            <span className="text-micro text-ink-secondary">{formatCell(point.value, kind)}</span>
            <span
              aria-hidden="true"
              className="w-full max-w-12 rounded-t-sm bg-brand-violet"
              style={{ height: Math.max(MIN_BAR, (point.value / peak) * BAR_AREA) }}
            />
            <span className="max-w-full truncate text-micro text-ink-tertiary">{point.label}</span>
          </li>
        ))}
      </ul>
    </Panel>
  )
}
