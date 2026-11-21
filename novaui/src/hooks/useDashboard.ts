import { useEffect, useState } from 'react'
import { loadDashboard } from '../services/dashboardLoader'
import { readSession } from '../services/sessionStore'
import type { DashboardData, PeriodKey } from '../types/dashboard'
import { useApiError } from './useApiError'

export const useDashboard = (period: PeriodKey) => {
  const handleError = useApiError()
  const [data, setData] = useState<DashboardData | null>(null)

  useEffect(() => {
    const session = readSession()
    if (!session) return
    let active = true
    loadDashboard(session.role, period)
      .then((result) => active && setData(result))
      .catch((error: unknown) => active && handleError(error))
    return () => {
      active = false
    }
  }, [period, handleError])

  return { data, loading: data === null }
}
