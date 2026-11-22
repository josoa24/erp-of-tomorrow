import { httpGet, type RequestOptions } from '../../../services/api/httpClient'
import type { TeamSummary } from '../types/teamSummary'

export const TEAM_SUMMARY_PATH = '/api/rh/summary'

export const getTeamSummary = (options: RequestOptions = {}): Promise<TeamSummary> =>
  httpGet<TeamSummary>(TEAM_SUMMARY_PATH, options)
