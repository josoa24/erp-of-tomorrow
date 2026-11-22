import type { KpiItem } from '../../../components/module/types'
import type { Role } from '../../../types/auth'
import { canAccessModule } from '../../../utils/roleAccess'
import type { useHomeData } from '../hooks/useHomeData'
import { deliveryIndicator, salesIndicator, stockIndicator, teamIndicator } from './homeIndicators'

export const homeIndicators = (roles: readonly Role[], data: ReturnType<typeof useHomeData>): KpiItem[] => {
  const delivery = data.deliveries.data
  return [
    ...(canAccessModule(roles, 'vente') ? [salesIndicator(data.sales)] : []),
    ...(canAccessModule(roles, 'stock') ? [stockIndicator(data.stockSummary)] : []),
    ...(delivery ? [deliveryIndicator(delivery)] : canAccessModule(roles, 'rh') ? [teamIndicator(data.team)] : []),
  ]
}
