import { MODULE_NAV_ITEMS, type NavItem } from '../constants/moduleNav'
import { canAccessModule, type RoleInput } from '../utils/roleAccess'

export const BOTTOM_BAR_MODULE_SLOTS = 2

export const visibleNavItems = (roles: RoleInput, items: readonly NavItem[] = MODULE_NAV_ITEMS): NavItem[] =>
  items.filter((item) => item.module === null || canAccessModule(roles, item.module))

export const splitForBottomBar = (items: readonly NavItem[], slots = BOTTOM_BAR_MODULE_SLOTS) => {
  const home = items.filter((item) => item.module === null)
  const modules = items.filter((item) => item.module !== null)
  return { primary: [...home, ...modules.slice(0, slots)], overflow: modules.slice(slots) }
}
