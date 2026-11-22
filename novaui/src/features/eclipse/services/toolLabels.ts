import type { AgentToolCall } from '../types/agent'

const LABELS: Record<string, string> = {
  search_products: 'les produits',
  get_stock_status: 'l’état du stock',
  get_stock_summary: 'la synthèse du stock',
  get_product_autonomy: 'l’autonomie du produit',
  get_product_movements: 'les mouvements du produit',
  search_suppliers: 'les fournisseurs',
  list_purchase_orders: 'les bons de commande',
  get_sales_summary: 'les ventes',
  get_top_products: 'les produits les plus vendus',
  search_sales: 'les ventes',
  search_customers: 'les clients',
  get_customer_profile: 'la fiche du client',
  get_hr_summary: 'l’équipe',
  search_employees: 'les employés',
}

export const THINKING_LABEL = 'Eclipse réfléchit…'

export const progressLabel = (calls: AgentToolCall[]): string => {
  const running = calls.find((call) => call.success === undefined)
  if (!running) return THINKING_LABEL
  return `Eclipse interroge ${LABELS[running.name] ?? 'l’ERP'}…`
}
