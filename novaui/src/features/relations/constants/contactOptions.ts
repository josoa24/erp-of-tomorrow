import { Building2, Store, User } from 'lucide-react'
import type { CustomerCategory, PartyType } from '../types/contact'
import type { ChoiceOption } from '../types/choiceOption'

export const CONTACT_KIND_OPTIONS: readonly ChoiceOption<PartyType>[] = [
  { value: 'CUSTOMER', label: 'Client', icon: Store },
  { value: 'SUPPLIER', label: 'Fournisseur', icon: Building2 },
]

export const CUSTOMER_CATEGORY_OPTIONS: readonly ChoiceOption<CustomerCategory>[] = [
  { value: 'PARTICULIER', label: 'Particulier', icon: User },
  { value: 'ENTREPRISE', label: 'Professionnel', icon: Building2 },
]
