import { useQuery } from '@tanstack/react-query'
import { getPurchaseOrders, getSuppliers } from '../api/purchaseOrdersApi'
import { STOCK_QUERY_KEY } from './stockQueryKeys'
import { useErrorRedirect } from './useErrorRedirect'

const NONE: never[] = []

export const useOrders = () => {
  const orders = useQuery({ queryKey: [...STOCK_QUERY_KEY, 'orders'], queryFn: ({ signal }) => getPurchaseOrders({ signal }) })
  const suppliers = useQuery({ queryKey: [...STOCK_QUERY_KEY, 'suppliers'], queryFn: ({ signal }) => getSuppliers({ signal }), staleTime: 60_000 })
  useErrorRedirect(orders.error ?? suppliers.error)

  return { orders: orders.data ?? NONE, suppliers: suppliers.data ?? NONE, loading: orders.isPending }
}
