import type { SaleRowHandlers } from '../services/salesRowActions'
import { useDeleteDraft, useValidateDraft } from './useSaleCommands'
import type { useSalesDialogs } from './useSalesDialogs'

type Dialogs = ReturnType<typeof useSalesDialogs>

export const useSaleRowHandlers = ({ openForm, openCollect, openCancel, openDetail }: Dialogs): SaleRowHandlers => {
  const validate = useValidateDraft()
  const remove = useDeleteDraft()

  return {
    edit: openForm,
    validate: (sale) => validate.mutate(sale),
    delete: (sale) => remove.mutate(sale),
    collect: openCollect,
    cancel: openCancel,
    detail: openDetail,
  }
}
