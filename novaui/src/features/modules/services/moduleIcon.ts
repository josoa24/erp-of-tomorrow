import {
  CalendarDays,
  ClipboardList,
  LayoutGrid,
  Package,
  Receipt,
  Truck,
  Users,
  Wrench,
  type LucideIcon,
} from 'lucide-react'

const MODULE_ICONS: Record<string, LucideIcon> = {
  truck: Truck,
  wrench: Wrench,
  'calendar-days': CalendarDays,
  package: Package,
  'clipboard-list': ClipboardList,
  'layout-grid': LayoutGrid,
  users: Users,
  receipt: Receipt,
}

export const moduleIconFor = (name: string): LucideIcon => MODULE_ICONS[name] ?? LayoutGrid
