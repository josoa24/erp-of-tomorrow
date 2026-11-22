import { Mail, MapPin, Phone, StickyNote, Users } from 'lucide-react'
import type { ChoiceOption } from '../types/choiceOption'
import type { InteractionType } from '../types/interaction'

export const INTERACTION_TYPES: readonly ChoiceOption<InteractionType>[] = [
  { value: 'APPEL', label: 'Appel', icon: Phone },
  { value: 'EMAIL', label: 'E-mail', icon: Mail },
  { value: 'REUNION', label: 'Réunion', icon: Users },
  { value: 'VISITE', label: 'Visite', icon: MapPin },
  { value: 'NOTE', label: 'Note', icon: StickyNote },
]
