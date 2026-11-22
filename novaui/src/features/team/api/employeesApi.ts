import { httpGet, httpPost, type RequestOptions } from '../../../services/api/httpClient'
import { buildQuery } from '../../../services/api/queryString'
import type { Employee, EmployeeCounts, EmployeeCreateRequest } from '../types/employee'

export const EMPLOYEES_PATH = '/api/rh/employees'

export const getEmployees = (search: string, options: RequestOptions = {}): Promise<Employee[]> =>
  httpGet<Employee[]>(`${EMPLOYEES_PATH}${buildQuery({ search })}`, options)

export const getEmployeeCounts = (options: RequestOptions = {}): Promise<EmployeeCounts> =>
  httpGet<EmployeeCounts>(`${EMPLOYEES_PATH}/counts`, options)

export const createEmployee = (request: EmployeeCreateRequest): Promise<Employee> =>
  httpPost<Employee>(EMPLOYEES_PATH, request)
