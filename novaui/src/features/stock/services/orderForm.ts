export interface OrderLineDraft {
  key: number
  productId: number | null
  productLabel: string
  quantity: string
  unitPrice: string
}

export const emptyLine = (key: number): OrderLineDraft => ({ key, productId: null, productLabel: '', quantity: '', unitPrice: '' })

const toNumber = (text: string): number => Number(text.replace(/\s/g, '').replace(',', '.'))

export const lineTotal = (line: OrderLineDraft): number => {
  const total = toNumber(line.quantity) * toNumber(line.unitPrice)
  return Number.isFinite(total) ? total : 0
}

export const isLineValid = (line: OrderLineDraft): boolean =>
  line.productId !== null && Number.isInteger(toNumber(line.quantity)) && toNumber(line.quantity) > 0 && toNumber(line.unitPrice) >= 0 && line.unitPrice.trim() !== ''

export const isOrderValid = (supplierId: number | null, lines: OrderLineDraft[]): boolean =>
  supplierId !== null && lines.length > 0 && lines.every(isLineValid)

export const orderTotal = (lines: OrderLineDraft[]): number => lines.reduce((sum, line) => sum + lineTotal(line), 0)

export const toRequest = (supplierId: number, lines: OrderLineDraft[]) => ({
  supplierId,
  lines: lines.map((line) => ({ productId: line.productId ?? 0, quantity: toNumber(line.quantity), unitPrice: toNumber(line.unitPrice) })),
})
