import { PackageSearch } from 'lucide-react'
import { useMemo } from 'react'
import { DataTable } from '../../../components/module/DataTable'
import { ListCard } from '../../../components/module/ListCard'
import { ModulePage } from '../../../components/module/ModulePage'
import { Button } from '../../../components/ui/button'
import { EmptyState } from '../../../components/ui/empty-state'
import { OrderDialogs } from '../components/OrderDialogs'
import { orderColumns } from '../components/orderColumns'
import { useOrderDialogs } from '../hooks/useOrderDialogs'
import { useOrders } from '../hooks/useOrders'
import { orderActions } from '../services/orderActions'
import { orderKpis } from '../services/orderKpis'

export const PurchaseOrdersScreen = () => {
  const { orders, suppliers, loading } = useOrders()
  const { dialog, open, close } = useOrderDialogs()
  const columns = useMemo(() => orderColumns(suppliers), [suppliers])
  const handlers = useMemo(
    () => ({
      onView: (order: (typeof orders)[number]) => open({ type: 'view', order }),
      onValidate: (order: (typeof orders)[number]) => open({ type: 'validate', order }),
      onReceive: (order: (typeof orders)[number]) => open({ type: 'receive', order }),
    }),
    [open],
  )

  return (
    <ModulePage
      title="Commandes"
      subtitle="Commandes fournisseurs : brouillon, validation, réception."
      primaryAction={<Button onClick={() => open({ type: 'create' })}>Nouvelle commande</Button>}
      kpis={orderKpis(orders)}
      kpiLabel="Indicateurs des commandes"
    >
      <ListCard label="Commandes fournisseurs">
        <DataTable
          caption="Commandes fournisseurs, les plus récentes d’abord"
          columns={columns}
          rows={orders}
          getRowKey={(order) => order.id}
          getRowLabel={(order) => `commande ${order.id}`}
          rowActions={(order) => orderActions(order, handlers)}
          loading={loading}
          empty={
            <EmptyState
              icon={PackageSearch}
              title="Aucune commande pour l’instant"
              description="Créez une commande fournisseur, ou demandez à Eclipse de la préparer."
              actions={<Button onClick={() => open({ type: 'create' })}>Nouvelle commande</Button>}
            />
          }
        />
      </ListCard>
      <OrderDialogs dialog={dialog} suppliers={suppliers} onClose={close} />
    </ModulePage>
  )
}
