import { CalendarDays, Truck, Wrench, type LucideIcon } from 'lucide-react'

export interface ModuleExample {
  label: string
  icon: LucideIcon
  text: string
}

export const DESCRIPTION_MAX = 500

export const MODULE_EXAMPLES: readonly ModuleExample[] = [
  {
    label: 'Livraisons',
    icon: Truck,
    text: 'Je veux suivre mes livraisons : client, quartier, livreur, heure prévue et statut.',
  },
  {
    label: 'Réparations',
    icon: Wrench,
    text: 'Je veux suivre les réparations : appareil, client, panne, technicien et statut.',
  },
  {
    label: 'Rendez-vous',
    icon: CalendarDays,
    text: 'Je veux gérer mes rendez-vous : client, date, heure, motif et statut.',
  },
]
