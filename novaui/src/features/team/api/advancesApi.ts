import { httpGet, httpPost, type RequestOptions } from '../../../services/api/httpClient'
import { buildQuery } from '../../../services/api/queryString'
import type { Advance, AdvanceCreateRequest, AdvanceStatus, AdvanceSummary } from '../types/advance'
import type { Decision } from '../types/decision'

export const ADVANCES_PATH = '/api/rh/advances'

export interface AdvanceQuery {
  employeeId: number
  status: AdvanceStatus
}

export const getAdvances = ({ employeeId, status }: AdvanceQuery, options: RequestOptions = {}): Promise<Advance[]> =>
  httpGet<Advance[]>(`${ADVANCES_PATH}${buildQuery({ employeeId, status })}`, options)

export const getAdvanceSummary = (options: RequestOptions = {}): Promise<AdvanceSummary> =>
  httpGet<AdvanceSummary>(`${ADVANCES_PATH}/summary`, options)

export const createAdvance = (request: AdvanceCreateRequest): Promise<Advance> =>
  httpPost<Advance>(ADVANCES_PATH, request)

export const decideAdvance = (id: number, decision: Decision): Promise<Advance> =>
  httpPost<Advance>(`${ADVANCES_PATH}/${id}/${decision}`)
