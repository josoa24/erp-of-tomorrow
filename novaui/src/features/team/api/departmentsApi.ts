import { httpGet, type RequestOptions } from '../../../services/api/httpClient'
import type { Department } from '../types/employee'

export const DEPARTMENTS_PATH = '/api/rh/departments'

export const getDepartments = (options: RequestOptions = {}): Promise<Department[]> =>
  httpGet<Department[]>(DEPARTMENTS_PATH, options)
