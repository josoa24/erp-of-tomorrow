import { httpPost } from '../../../services/api/httpClient'
import type { ModuleDefinitionRequest } from '../types/moduleDefinition'
import type { ModuleProposal } from '../types/moduleRecord'

export const PROPOSE_MODULE_PATH = '/api/ai/eclipse/modules/propose'

export const proposeModule = (description: string): Promise<ModuleProposal> =>
  httpPost<ModuleProposal>(PROPOSE_MODULE_PATH, { description })

export const REFINE_MODULE_PATH = '/api/ai/eclipse/modules/refine'

export const refineModule = (proposal: ModuleDefinitionRequest, instruction: string): Promise<ModuleProposal> =>
  httpPost<ModuleProposal>(REFINE_MODULE_PATH, { proposal, instruction })
