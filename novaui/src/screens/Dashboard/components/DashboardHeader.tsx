import { EclipseLogo } from '../../../components/common/EclipseLogo'
import type { PeriodKey } from '../../../types/dashboard'
import { HeaderActions } from './HeaderActions'
import { NavPill } from './NavPill'
import { PeriodSelect } from './PeriodSelect'

interface DashboardHeaderProps {
  period: PeriodKey
  hasAlerts: boolean
  onPeriodChange: (period: PeriodKey) => void
}

export const DashboardHeader = ({ period, hasAlerts, onPeriodChange }: DashboardHeaderProps) => (
  <header className="grid grid-cols-[1fr_auto_1fr] items-center gap-6 max-[1100px]:flex max-[1100px]:flex-wrap max-[1100px]:justify-between max-[1100px]:gap-y-4.5 max-[1100px]:[&>nav]:order-3 max-[1100px]:[&>nav]:mx-auto">
    <EclipseLogo />
    <NavPill />
    <div className="flex items-center gap-5.5 justify-self-end max-[1100px]:gap-3">
      <PeriodSelect value={period} onChange={onPeriodChange} />
      <HeaderActions hasAlerts={hasAlerts} />
    </div>
  </header>
)
