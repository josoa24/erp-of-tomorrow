import { Panel } from '../ui/panel'
import { Stat } from '../ui/stat'
import type { KpiItem } from './types'

export interface KpiStripProps {
  items: KpiItem[]
  label?: string
}

export const KpiStrip = ({ items, label = 'Indicateurs' }: KpiStripProps) => (
  <Panel aria-label={label} className="grid grid-cols-1 items-center gap-4 sm:grid-cols-2 lg:flex">
    {items.map(({ key, ...stat }) => (
      <Stat key={key} {...stat} />
    ))}
  </Panel>
)
