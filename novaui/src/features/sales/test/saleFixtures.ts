import type { Sale } from '../types/sale'

export const SALE: Sale = {
  id: 412,
  number: 'V-0412',
  customerId: 3,
  customerName: 'Épicerie Rova',
  status: 'VALIDATED',
  paymentStatus: 'TO_COLLECT',
  totalAmount: 186000,
  paidAmount: 0,
  paymentMethod: 'MVOLA',
  creditDays: null,
  dueDate: '2026-11-24',
  toDeliver: false,
  lines: [{ productId: 7, productName: 'Riz blanc 50 kg', quantity: 1, unitPrice: 186000, lineTotal: 186000 }],
  createdAt: '2026-11-24T07:42:00Z',
  validatedAt: '2026-11-24T07:43:00Z',
}

export const saleWith = (patch: Partial<Sale>): Sale => ({ ...SALE, ...patch })

export const RIZ = { id: 7, name: 'Riz blanc 50 kg', sku: 'RIZ-50', unit: 'sac', unitPrice: 186000, quantity: 14 }
export const HUILE = { id: 9, name: 'Huile 5 L', sku: 'HUI-5', unit: 'bidon', unitPrice: 42000, quantity: 3 }
export const HERY = { id: 3, name: 'Hery Rabe', city: 'Antananarivo', phone: '034 12 345 67' }
