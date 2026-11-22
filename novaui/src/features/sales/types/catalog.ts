export interface CatalogProduct {
  id: number
  name: string
  sku: string
  unit: string
  unitPrice: number
  quantity: number
}

export interface CustomerOption {
  id: number
  name: string
  city: string | null
  phone: string | null
}
