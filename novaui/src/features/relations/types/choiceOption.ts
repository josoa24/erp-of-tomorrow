import type { LucideIcon } from 'lucide-react'

export interface ChoiceOption<Value extends string> {
  value: Value
  label: string
  icon?: LucideIcon
}
