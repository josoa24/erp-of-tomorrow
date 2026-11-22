import { isAdaptationKey } from '../features/eclipse/constants/adaptations'
import type { AdaptationKey } from '../features/eclipse/types/adaptation'

export const CREATE_MODULE_SEARCH_VALUE = 'module'

export interface ShellSearch {
  creer?: typeof CREATE_MODULE_SEARCH_VALUE
  eclipse?: AdaptationKey
}

export const validateShellSearch = (search: Record<string, unknown>): ShellSearch => ({
  ...(search.creer === CREATE_MODULE_SEARCH_VALUE && { creer: CREATE_MODULE_SEARCH_VALUE }),
  ...(isAdaptationKey(search.eclipse) && { eclipse: search.eclipse }),
})
