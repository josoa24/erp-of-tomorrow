import { EclipseLogo } from '../../../components/common/EclipseLogo'
import type { PeriodKey } from '../../../types/dashboard'
import { HeaderActions } from './HeaderActions'
import { NavPill } from './NavPill'
import { PeriodSelect } from './PeriodSelect'
import styles from './DashboardHeader.module.css'

interface DashboardHeaderProps {
  period: PeriodKey
  hasAlerts: boolean
  onPeriodChange: (period: PeriodKey) => void
}

export const DashboardHeader = ({ period, hasAlerts, onPeriodChange }: DashboardHeaderProps) => (
  <header className={styles.header}>
    <EclipseLogo />
    <NavPill />
    <div className={styles.right}>
      <PeriodSelect value={period} onChange={onPeriodChange} />
      <HeaderActions hasAlerts={hasAlerts} />
    </div>
  </header>
)
