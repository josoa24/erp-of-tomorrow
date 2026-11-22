import { visibleNavItems } from '../../../services/navigation'
import type { Role } from '../../../types/auth'
import { ROLE_LABELS } from '../../../constants/roleLabels'

export const accessSentence = (role: Role): string =>
  role === 'ADMIN'
    ? 'Administrateur : vous accédez à tous les espaces et à tous les modules créés.'
    : `Rôle ${ROLE_LABELS[role]} : seuls les espaces autorisés apparaissent, dans la navigation comme ici.`

export const accessLabels = (role: Role, moduleNames: string[]): string[] => [
  ...visibleNavItems(role).map((item) => item.label),
  ...moduleNames,
]
