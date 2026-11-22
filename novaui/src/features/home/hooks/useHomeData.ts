import { getStockSummary } from '../../stock/api/stockSummaryApi'
import { getLeavesByStatus } from '../../team/api/leavesApi'
import { getTeamSummary } from '../../team/api/teamSummaryApi'
import type { Role } from '../../../types/auth'
import { canAccessModule } from '../../../utils/roleAccess'
import { getEclipseSuggestions, getReplenishment, getSalesKpis } from '../api/homeApi'
import { useHomeQuery } from './useHomeQuery'

export const useHomeData = (role: Role) => {
  const vente = canAccessModule(role, 'vente')
  const stock = canAccessModule(role, 'stock')
  const rh = canAccessModule(role, 'rh')
  return {
    sales: useHomeQuery('sales-kpis', (signal) => getSalesKpis({ signal }), vente),
    stockSummary: useHomeQuery('stock-summary', (signal) => getStockSummary({ signal }), stock),
    team: useHomeQuery('team-summary', (signal) => getTeamSummary({ signal }), rh),
    replenishment: useHomeQuery('replenishment', (signal) => getReplenishment({ signal }), stock),
    leaves: useHomeQuery('leaves-draft', (signal) => getLeavesByStatus('DRAFT', { signal }), rh),
    suggestions: useHomeQuery('suggestions', (signal) => getEclipseSuggestions({ signal }), true),
  }
}
