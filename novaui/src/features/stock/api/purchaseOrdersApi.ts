import { httpGet, httpPost, type RequestOptions } from '../../../services/api/httpClient'
import type { PurchaseOrder, PurchaseOrderRequest, Supplier } from '../types/purchaseOrder'

export const ORDERS_PATH = '/api/stock/purchase-orders'

export const getPurchaseOrders = (options: RequestOptions = {}): Promise<PurchaseOrder[]> =>
  httpGet<PurchaseOrder[]>(ORDERS_PATH, options)

export const createPurchaseOrder = (request: PurchaseOrderRequest): Promise<PurchaseOrder> =>
  httpPost<PurchaseOrder>(ORDERS_PATH, request)

export const validatePurchaseOrder = (id: number): Promise<PurchaseOrder> =>
  httpPost<PurchaseOrder>(`${ORDERS_PATH}/${id}/validate`)

export const receivePurchaseOrder = (id: number): Promise<PurchaseOrder> =>
  httpPost<PurchaseOrder>(`${ORDERS_PATH}/${id}/receive`)

export const getSuppliers = (options: RequestOptions = {}): Promise<Supplier[]> =>
  httpGet<Supplier[]>('/api/stock/suppliers', options)
