import { toStockAdaptationView } from '../stockAdaptation'

describe('toStockAdaptationView', () => {
  it('shouldEnableAndHighlight_whilePreviewing', () => {
    expect(toStockAdaptationView(false, true)).toEqual({ enabled: true, highlighted: true })
  })

  it('shouldEnableWithoutHighlight_whenApplied', () => {
    expect(toStockAdaptationView(true, false)).toEqual({ enabled: true, highlighted: false })
  })

  it('shouldStayGeneric_whenNeitherActiveNorPreviewing', () => {
    expect(toStockAdaptationView(false, false)).toEqual({ enabled: false, highlighted: false })
  })
})
