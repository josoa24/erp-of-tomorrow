import { MessageSquare, ShoppingCart, Truck, type LucideIcon } from 'lucide-react'
import type { TimelineKind } from '../types/interaction'

export const TIMELINE_KIND_ICONS: Record<TimelineKind, LucideIcon> = {
  INTERACTION: MessageSquare,
  SALE: ShoppingCart,
  PURCHASE_ORDER: Truck,
}
