import type { UiBlock } from '../../types/uiBlock'
import { AlertsBlock } from './AlertsBlock'
import { CardBlock } from './CardBlock'
import { ChartBlock } from './ChartBlock'
import { KpiBlock } from './KpiBlock'
import { TableBlock } from './TableBlock'

export const UiBlockView = ({ block }: { block: UiBlock }) => {
  switch (block.type) {
    case 'kpi':
      return <KpiBlock title={block.title} items={block.items} />
    case 'table':
      return <TableBlock title={block.title} columns={block.columns} rows={block.rows} total={block.total} />
    case 'card':
      return <CardBlock title={block.title} subtitle={block.subtitle} tone={block.tone} fields={block.fields} />
    case 'alerts':
      return <AlertsBlock title={block.title} items={block.items} />
    case 'chart':
      return <ChartBlock title={block.title} kind={block.kind} points={block.points} />
  }
}
