import { ADAPTATION_CATALOG } from '../../constants/adaptations'
import type { AdaptationProposal } from '../../types/adaptation'
import { belongsToModule, resolvePreview, withPreview, withoutPreview } from '../adaptationPreview'

const LOTS_IN_OWN_WORDS: AdaptationProposal = {
  ...ADAPTATION_CATALOG['stock.lots-peremption'],
  request: 'Je veux suivre les lots de riz et leur date limite',
}

describe('adaptationPreview', () => {
  it('shouldResolveNothing_whenTheSearchHasNoAdaptation', () => {
    expect(resolvePreview(undefined, { stock: LOTS_IN_OWN_WORDS })).toBeNull()
  })

  it('shouldUseCatalogTexts_whenNoProposalWasStarted', () => {
    expect(resolvePreview('ventes.mobile-money-credit', {})).toBe(ADAPTATION_CATALOG['ventes.mobile-money-credit'])
  })

  it('shouldKeepTheStartedProposal_whenItMatchesTheSearch', () => {
    expect(resolvePreview('stock.lots-peremption', { stock: LOTS_IN_OWN_WORDS })).toBe(LOTS_IN_OWN_WORDS)
  })

  it('shouldTellWhichModuleTheSearchBelongsTo', () => {
    expect(belongsToModule('equipe.avances-salaire', 'equipe')).toBe(true)
    expect(belongsToModule('equipe.avances-salaire', 'stock')).toBe(false)
    expect(belongsToModule(undefined, 'stock')).toBe(false)
  })

  it('shouldStoreAndForgetProposalsPerModule', () => {
    const stored = withPreview({}, LOTS_IN_OWN_WORDS)

    expect(stored).toEqual({ stock: LOTS_IN_OWN_WORDS })
    expect(withoutPreview(stored, 'stock')).toEqual({})
  })
})
