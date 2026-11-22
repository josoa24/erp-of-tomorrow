import { ShieldCheck, UserCheck, Users } from 'lucide-react'
import type { KpiItem } from '../../../components/module/types'
import type { UserCounts } from '../types/user'

export const KPI_PLACEHOLDER = '—'

export const buildUsersKpiItems = (counts: UserCounts | null): KpiItem[] => [
  { key: 'all', icon: Users, label: 'Comptes', value: counts ? counts.all : KPI_PLACEHOLDER, hint: counts ? `${counts.disabled} désactivés` : undefined },
  {
    key: 'active',
    icon: UserCheck,
    tone: 'mint',
    label: 'Actifs',
    value: counts ? counts.active : KPI_PLACEHOLDER,
    hint: counts ? `sur ${counts.all}` : undefined,
  },
  { key: 'admins', icon: ShieldCheck, tone: 'blush', label: 'Administrateurs', value: counts ? counts.admins : KPI_PLACEHOLDER, hint: counts ? 'accès complet' : undefined },
]

export const usersSubtitle = (counts: UserCounts | null): string | undefined =>
  counts ? `${counts.all} ${counts.all > 1 ? 'comptes' : 'compte'} · ${counts.active} ${counts.active > 1 ? 'actifs' : 'actif'}` : undefined
