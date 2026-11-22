import { httpGet, httpPost, type RequestOptions } from '../../../services/api/httpClient'
import { buildQuery } from '../../../services/api/queryString'
import type { Decision } from '../types/decision'
import type { Leave, LeaveCreateRequest, LeaveStatus } from '../types/leave'

export const LEAVES_PATH = '/api/rh/leaves'

export const getLeavesByStatus = (status: LeaveStatus, options: RequestOptions = {}): Promise<Leave[]> =>
  httpGet<Leave[]>(`${LEAVES_PATH}${buildQuery({ status })}`, options)

export const createLeave = (request: LeaveCreateRequest): Promise<Leave> => httpPost<Leave>(LEAVES_PATH, request)

export const decideLeave = (id: number, decision: Decision): Promise<Leave> =>
  httpPost<Leave>(`${LEAVES_PATH}/${id}/${decision}`)
