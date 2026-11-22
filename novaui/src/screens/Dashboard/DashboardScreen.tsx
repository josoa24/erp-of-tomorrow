import { useState } from 'react'
import { SpaceBackground } from '../../components/common/SpaceBackground'
import { useDashboard } from '../../hooks/useDashboard'
import type { PeriodKey } from '../../types/dashboard'
import { DashboardHeader } from './components/DashboardHeader'
import { DashboardStage } from './components/DashboardStage'
import { Greeting } from './components/Greeting'

export const DashboardScreen = () => {
  const [period, setPeriod] = useState<PeriodKey>('month')
  const { data } = useDashboard(period)

  return (
    <SpaceBackground variant="dashboard">
      <div className="mx-auto max-w-420 px-12 pt-7.5 max-sm:px-4 max-sm:pt-5">
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
