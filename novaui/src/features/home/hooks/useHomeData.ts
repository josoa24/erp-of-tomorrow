import { getStockSummary } from '../../stock/api/stockSummaryApi'
import { getLeavesByStatus } from '../../team/api/leavesApi'
import { getTeamSummary } from '../../team/api/teamSummaryApi'
import type { Role } from '../../../types/auth'
import { canAccessModule } from '../../../utils/roleAccess'
import { loadDeliveryIndicator } from '../services/deliveryIndicator'
import { getEclipseSuggestions, getReplenishment, getSalesKpis } from '../api/homeApi'
import { useHomeQuery } from './useHomeQuery'

export const useHomeData = (roles: readonly Role[]) => {
  const vente = canAccessModule(roles, 'vente')
  const stock = canAccessModule(roles, 'stock')
  const rh = canAccessModule(roles, 'rh')
  return {
    sales: useHomeQuery('sales-kpis', (signal) => getSalesKpis({ signal }), vente),
    stockSummary: useHomeQuery('stock-summary', (signal) => getStockSummary({ signal }), stock),
    team: useHomeQuery('team-summary', (signal) => getTeamSummary({ signal }), rh),
    replenishment: useHomeQuery('replenishment', (signal) => getReplenishment({ signal }), stock),
    leaves: useHomeQuery('leaves-draft', (signal) => getLeavesByStatus('DRAFT', { signal }), rh),
    deliveries: useHomeQuery('deliveries', (signal) => loadDeliveryIndicator({ signal }), true),
    suggestions: useHomeQuery('suggestions', (signal) => getEclipseSuggestions({ signal }), true),
  }
}
