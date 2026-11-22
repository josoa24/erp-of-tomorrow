import { Banknote, Hash, Percent, type LucideIcon } from 'lucide-react'
import { Panel } from '../../../../components/ui/panel'
import { Stat, type StatTone } from '../../../../components/ui/stat'
import { formatCell } from '../../services/blockFormat'
import type { UiCellKind, UiKpiItem } from '../../types/uiBlock'

const ICONS: Record<UiCellKind, LucideIcon> = {
  money: Banknote,
  percent: Percent,
  number: Hash,
  text: Hash,
  date: Hash,
  status: Hash,
}

const TONES: StatTone[] = ['lilac', 'mint', 'blush']

export const KpiBlock = ({ title, items }: { title: string; items: UiKpiItem[] }) => (
  <Panel aria-label={title} padding="m" className="flex flex-col gap-3 bg-surface-solid">
    <h4 className="text-label text-ink-secondary">{title}</h4>
    <div className="grid grid-cols-2 gap-4 sm:grid-cols-4">
      {items.map((item, index) => (
        <Stat
          key={item.label}
          icon={ICONS[item.kind]}
          tone={TONES[index % TONES.length]}
          label={item.label}
          value={formatCell(item.value, item.kind)}
          hint={item.hint}
        />
      ))}
    </div>
  </Panel>
)
