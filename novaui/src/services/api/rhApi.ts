import type { HrSummary } from '../../types/hr'
import { httpGet } from './httpClient'

export const getHrSummary = (): Promise<HrSummary> => httpGet<HrSummary>('/api/rh/summary')
