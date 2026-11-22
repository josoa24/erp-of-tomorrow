import type { LucideIcon } from 'lucide-react'
import { ROLE_LABELS } from '../../../constants/roleLabels'
import { visibleNavItems } from '../../../services/navigation'
import type { Role } from '../../../types/auth'
import type { ModuleDefinitionSummary } from '../../modules/types/moduleDefinition'
import { moduleIconFor } from '../../modules/services/moduleIcon'

export interface AccessChip {
  label: string
  icon: LucideIcon
}

export const accessSentence = (role: Role): string =>
  role === 'ADMIN'
    ? 'Administrateur : vous accédez à tous les espaces et à tous les modules créés.'
    : `Rôle ${ROLE_LABELS[role]} : seuls les espaces autorisés apparaissent, dans la navigation comme ici.`

export const accessChips = (role: Role, modules: ModuleDefinitionSummary[]): AccessChip[] => [
  ...visibleNavItems(role).map((item) => ({ label: item.label, icon: item.icon })),
  ...modules.map((module) => ({ label: module.name, icon: moduleIconFor(module.icon) })),
]
