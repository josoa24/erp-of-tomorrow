import { httpPost } from '../../../services/api/httpClient'
import type { MovementRequest, MovementResponse } from '../types/stock'
import { productPath } from './stockProductsApi'

export const recordMovement = (productId: number, movement: MovementRequest): Promise<MovementResponse> =>
  httpPost<MovementResponse>(`${productPath(productId)}/movements`, movement)
