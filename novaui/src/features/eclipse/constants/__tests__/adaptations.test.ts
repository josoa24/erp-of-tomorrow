import { ADAPTATION_CATALOG, ADAPTATION_KEYS, ADAPTATION_MODULE, isAdaptationKey } from '../adaptations'

describe('adaptations catalog', () => {
  it.each(ADAPTATION_KEYS)('shouldDescribe_%s_forItsOwnModule', (key) => {
    const proposal = ADAPTATION_CATALOG[key]

    expect(proposal.adaptationKey).toBe(key)
    expect(proposal.moduleKey).toBe(ADAPTATION_MODULE[key])
    expect(proposal.summary.startsWith('Eclipse a ajouté : ')).toBe(true)
  })

  it('shouldRecognizeOnlyCatalogKeys', () => {
    expect(isAdaptationKey('stock.lots-peremption')).toBe(true)
    expect(isAdaptationKey('stock.inconnue')).toBe(false)
    expect(isAdaptationKey(42)).toBe(false)
  })
})
