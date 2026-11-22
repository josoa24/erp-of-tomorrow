import { useQuery } from '@tanstack/react-query'
import { useState } from 'react'
import { getSalesSummary } from '../../../services/api/venteApi'
import { getCreditSummary, getSalesKpis } from '../api/salesReportsApi'
import { monthToDate } from '../services/salesDates'
import { salesQueryKeys } from './salesQueryKeys'
import { useRedirectOnError } from './useRedirectOnError'

export const useSalesOverview = (withCredit: boolean) => {
  const [range] = useState(() => monthToDate())
  const kpis = useQuery({ queryKey: salesQueryKeys.kpis, queryFn: ({ signal }) => getSalesKpis({ signal }) })
  const month = useQuery({
    queryKey: salesQueryKeys.monthSummary(range.from, range.to),
    queryFn: () => getSalesSummary(range),
  })
  const credit = useQuery({
    queryKey: salesQueryKeys.credit,
    queryFn: ({ signal }) => getCreditSummary({ signal }),
    enabled: withCredit,
  })
  useRedirectOnError(kpis.error ?? month.error ?? credit.error)

  return {
    kpis: kpis.data ?? null,
    monthSalesCount: month.data?.salesCount ?? null,
    credit: credit.data ?? null,
  }
}
