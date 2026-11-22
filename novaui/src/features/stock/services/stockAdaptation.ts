export interface StockAdaptationView {
  enabled: boolean
  highlighted: boolean
}

export const GENERIC_STOCK_VIEW: StockAdaptationView = { enabled: false, highlighted: false }

export const toStockAdaptationView = (active: boolean, previewing: boolean): StockAdaptationView => ({
  enabled: active || previewing,
  highlighted: previewing,
})
