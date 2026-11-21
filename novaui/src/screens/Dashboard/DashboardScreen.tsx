import { useState } from 'react'
import { SpaceBackground } from '../../components/common/SpaceBackground'
import { useDashboard } from '../../hooks/useDashboard'
import type { PeriodKey } from '../../types/dashboard'
import { DashboardHeader } from './components/DashboardHeader'
import { DashboardStage } from './components/DashboardStage'
import { Greeting } from './components/Greeting'
import styles from './DashboardScreen.module.css'

export const DashboardScreen = () => {
  const [period, setPeriod] = useState<PeriodKey>('month')
  const { data } = useDashboard(period)

  return (
    <SpaceBackground variant="dashboard">
      <div className={styles.page}>
        <DashboardHeader
          period={period}
          onPeriodChange={setPeriod}
          hasAlerts={(data?.issues.length ?? 0) > 0}
        />
        <main>
          <Greeting />
          <DashboardStage data={data} />
        </main>
      </div>
    </SpaceBackground>
  )
}
