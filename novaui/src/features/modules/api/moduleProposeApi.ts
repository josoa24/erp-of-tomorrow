import { httpPost } from '../../../services/api/httpClient'
import type { ModuleProposal } from '../types/moduleRecord'

export const PROPOSE_MODULE_PATH = '/api/ai/eclipse/modules/propose'

export const proposeModule = (description: string): Promise<ModuleProposal> =>
  httpPost<ModuleProposal>(PROPOSE_MODULE_PATH, { description })
