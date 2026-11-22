export const CREATE_MODULE_SEARCH_VALUE = 'module'

export interface ShellSearch {
  creer?: typeof CREATE_MODULE_SEARCH_VALUE
}

export const validateShellSearch = (search: Record<string, unknown>): ShellSearch =>
  search.creer === CREATE_MODULE_SEARCH_VALUE ? { creer: CREATE_MODULE_SEARCH_VALUE } : {}
