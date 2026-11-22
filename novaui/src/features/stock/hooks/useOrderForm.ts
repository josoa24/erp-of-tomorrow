import { useCallback, useState } from 'react'
import { emptyLine, isOrderValid, orderTotal, toRequest, type OrderLineDraft } from '../services/orderForm'

export const useOrderForm = () => {
  const [supplierId, setSupplierId] = useState<number | null>(null)
  const [lines, setLines] = useState<OrderLineDraft[]>([emptyLine(0)])

  const change = useCallback((key: number, patch: Partial<OrderLineDraft>) => {
    setLines((previous) => previous.map((line) => (line.key === key ? { ...line, ...patch } : line)))
  }, [])
  const add = useCallback(() => setLines((previous) => [...previous, emptyLine(Math.max(...previous.map((line) => line.key)) + 1)]), [])
  const remove = useCallback((key: number) => setLines((previous) => (previous.length > 1 ? previous.filter((line) => line.key !== key) : previous)), [])

  return {
    supplierId,
    setSupplierId,
    lines,
    change,
    add,
    remove,
    total: orderTotal(lines),
    valid: isOrderValid(supplierId, lines),
    request: () => (supplierId === null ? null : toRequest(supplierId, lines)),
  }
}
