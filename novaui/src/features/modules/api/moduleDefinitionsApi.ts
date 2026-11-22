import { httpDelete, httpGet, httpPost, type RequestOptions } from '../../../services/api/httpClient'
import type { ModuleDefinition, ModuleDefinitionRequest, ModuleDefinitionSummary } from '../types/moduleDefinition'

export const MODULE_DEFINITIONS_PATH = '/api/modules/definitions'

export const getModuleDefinitions = (options: RequestOptions = {}): Promise<ModuleDefinitionSummary[]> =>
  httpGet<ModuleDefinitionSummary[]>(MODULE_DEFINITIONS_PATH, options)

export const getModuleDefinition = (slug: string, options: RequestOptions = {}): Promise<ModuleDefinition> =>
  httpGet<ModuleDefinition>(`${MODULE_DEFINITIONS_PATH}/${encodeURIComponent(slug)}`, options)

export const createModuleDefinition = (request: ModuleDefinitionRequest): Promise<ModuleDefinition> =>
  httpPost<ModuleDefinition>(MODULE_DEFINITIONS_PATH, request)

export const deleteModuleDefinition = (slug: string): Promise<void> =>
  httpDelete(`${MODULE_DEFINITIONS_PATH}/${encodeURIComponent(slug)}`)
