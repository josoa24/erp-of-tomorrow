import { httpGet, type RequestOptions } from '../../../services/api/httpClient'
import type { ModuleDefinitionSummary } from '../types/moduleDefinition'

export const MODULE_DEFINITIONS_PATH = '/api/modules/definitions'

export const getModuleDefinitions = (options: RequestOptions = {}): Promise<ModuleDefinitionSummary[]> =>
  httpGet<ModuleDefinitionSummary[]>(MODULE_DEFINITIONS_PATH, options)
