import { AudioLines, Home, Layers, Sparkles, Workflow, type LucideIcon } from 'lucide-react'

export interface NavItem {
  id: string
  label: string
  icon: LucideIcon
  available: boolean
}

export const NAV_ITEMS: NavItem[] = [
  { id: 'home', label: 'Accueil', icon: Home, available: true },
  { id: 'assistant', label: 'Assistant IA', icon: Sparkles, available: false },
  { id: 'flows', label: 'Processus', icon: Workflow, available: false },
  { id: 'modules', label: 'Modules', icon: Layers, available: false },
  { id: 'voice', label: 'Commande vocale', icon: AudioLines, available: false },
]
