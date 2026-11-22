import { httpGet, httpGetPage, httpPost, type Page, type RequestOptions } from '../../../services/api/httpClient'
import { buildQuery } from '../../../services/api/queryString'
import type { IndicatorValue, ModuleRecord, StatusCounts } from '../types/moduleRecord'
import { MODULE_DEFINITIONS_PATH } from './moduleDefinitionsApi'

export const RECORDS_PAGE_SIZE = 100

const base = (slug: string): string => `${MODULE_DEFINITIONS_PATH}/${encodeURIComponent(slug)}`

export interface RecordsQuery {
  search: string
  status?: string
}

export const getRecords = (slug: string, query: RecordsQuery, options: RequestOptions = {}): Promise<Page<ModuleRecord>> =>
  httpGetPage<ModuleRecord>(`${base(slug)}/records${buildQuery({ ...query, size: RECORDS_PAGE_SIZE })}`, options)

export const createRecord = (slug: string, values: Record<string, unknown>): Promise<ModuleRecord> =>
  httpPost<ModuleRecord>(`${base(slug)}/records`, { values })

export const advanceRecord = (slug: string, id: number): Promise<ModuleRecord> =>
  httpPost<ModuleRecord>(`${base(slug)}/records/${id}/advance`)

export const getIndicators = (slug: string, options: RequestOptions = {}): Promise<IndicatorValue[]> =>
  httpGet<IndicatorValue[]>(`${base(slug)}/indicators`, options)

export const getStatusCounts = (slug: string, options: RequestOptions = {}): Promise<StatusCounts> =>
  httpGet<StatusCounts>(`${base(slug)}/status-counts`, options)

export const getSalesImport = (slug: string, options: RequestOptions = {}): Promise<{ available: number }> =>
  httpGet<{ available: number }>(`${base(slug)}/sales-import`, options)

export const runSalesImport = (slug: string): Promise<{ created: number }> =>
  httpPost<{ created: number }>(`${base(slug)}/sales-import`)
