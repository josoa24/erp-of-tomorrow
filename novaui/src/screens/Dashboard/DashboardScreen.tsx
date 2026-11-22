import { useState } from 'react'
import { useDashboard } from '../../hooks/useDashboard'
import type { PeriodKey } from '../../types/dashboard'
import { DashboardStage } from './components/DashboardStage'
import { Greeting } from './components/Greeting'
import { PeriodSelect } from './components/PeriodSelect'

export const DashboardScreen = () => {
  const [period, setPeriod] = useState<PeriodKey>('month')
  const { data } = useDashboard(period)

  return (
    <section className="mx-auto max-w-[1376px] overflow-hidden rounded-xl bg-space-dashboard px-12 pt-7.5 text-nova-text max-sm:px-4 max-sm:pt-5">
      <div className="flex justify-end">
        <PeriodSelect value={period} onChange={setPeriod} />
      </div>
      <Greeting />
      <DashboardStage data={data} />
    </section>
  )
}
