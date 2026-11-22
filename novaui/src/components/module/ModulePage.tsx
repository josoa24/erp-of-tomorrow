import type { ReactNode } from 'react'
import { KpiStrip } from './KpiStrip'
import { PageHeader, type PageHeaderProps } from './PageHeader'
import type { KpiItem } from './types'

export interface ModulePageProps extends PageHeaderProps {
  kpis?: KpiItem[]
  kpiLabel?: string
  children?: ReactNode
}

export const ModulePage = ({ kpis, kpiLabel, children, ...header }: ModulePageProps) => (
  <div className="mx-auto flex w-full max-w-[1376px] flex-col gap-4">
    <div className="pb-1">
      <PageHeader {...header} />
    </div>
    {kpis && kpis.length > 0 && <KpiStrip items={kpis} label={kpiLabel} />}
    {children}
  </div>
)
