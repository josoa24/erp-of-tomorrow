import type { KpiItem } from '../../../components/module/types'
import type { Role } from '../../../types/auth'
import { canAccessModule } from '../../../utils/roleAccess'
import type { useHomeData } from '../hooks/useHomeData'
import { salesIndicator, stockIndicator, teamIndicator } from './homeIndicators'

export const homeIndicators = (role: Role, data: ReturnType<typeof useHomeData>): KpiItem[] => [
  ...(canAccessModule(role, 'vente') ? [salesIndicator(data.sales)] : []),
  ...(canAccessModule(role, 'stock') ? [stockIndicator(data.stockSummary)] : []),
  ...(canAccessModule(role, 'rh') ? [teamIndicator(data.team)] : []),
]
