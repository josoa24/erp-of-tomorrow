import { Handshake, Package, ShieldCheck, ShoppingCart, Users, type LucideIcon } from 'lucide-react'
import type { StatusTone } from '../components/ui/status-pill'
import type { Role } from '../types/auth'

export interface RoleStyle {
  icon: LucideIcon
  tone: StatusTone
  className?: string
}

export const ROLE_STYLES: Record<Role, RoleStyle> = {
  ADMIN: { icon: ShieldCheck, tone: 'info' },
  STOCK: { icon: Package, tone: 'warning' },
  VENTE: { icon: ShoppingCart, tone: 'success' },
  RH: { icon: Users, tone: 'neutral', className: 'bg-bg-blush text-ink-primary' },
  CRM: { icon: Handshake, tone: 'neutral' },
}
