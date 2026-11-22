import { Handshake, House, Package, ShoppingCart, Users, type LucideIcon } from 'lucide-react'
import { ROUTES, type ModuleRoutePath } from '../navigation/routes'
import type { ErpModule } from '../utils/roleAccess'

export type NavItemId = 'home' | 'sales' | 'stock' | 'team' | 'relations'

export interface NavItem {
  id: NavItemId
  to: ModuleRoutePath
  label: string
  icon: LucideIcon
  module: ErpModule | null
}

export const MODULE_NAV_ITEMS: readonly NavItem[] = [
  { id: 'home', to: ROUTES.home, label: 'Accueil', icon: House, module: null },
  { id: 'sales', to: ROUTES.sales, label: 'Ventes', icon: ShoppingCart, module: 'vente' },
  { id: 'stock', to: ROUTES.stock, label: 'Stock', icon: Package, module: 'stock' },
  { id: 'team', to: ROUTES.team, label: 'Équipe', icon: Users, module: 'rh' },
  { id: 'relations', to: ROUTES.relations, label: 'Relations', icon: Handshake, module: 'crm' },
]
