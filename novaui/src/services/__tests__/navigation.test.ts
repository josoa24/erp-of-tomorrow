import { MODULE_NAV_ITEMS } from '../../constants/moduleNav'
import { splitForBottomBar, visibleNavItems } from '../navigation'

const labels = (items: { label: string }[]) => items.map((item) => item.label)

describe('visibleNavItems', () => {
  it('shouldShowEveryModule_toAdmin', () => {
    expect(labels(visibleNavItems('ADMIN'))).toEqual(['Accueil', 'Ventes', 'Stock', 'Équipe', 'Relations'])
  })

  it.each([
    ['VENTE', ['Accueil', 'Ventes']],
    ['STOCK', ['Accueil', 'Stock']],
    ['RH', ['Accueil', 'Équipe']],
    ['CRM', ['Accueil', 'Relations']],
  ] as const)('shouldShowHomeAndOwnModule_to_%s', (role, expected) => {
    expect(labels(visibleNavItems(role))).toEqual(expected)
  })
})

describe('splitForBottomBar', () => {
  it('shouldKeepHomeAndTwoModules_andMoveTheRestToOverflow', () => {
    const { primary, overflow } = splitForBottomBar(MODULE_NAV_ITEMS)

    expect(labels(primary)).toEqual(['Accueil', 'Ventes', 'Stock'])
    expect(labels(overflow)).toEqual(['Équipe', 'Relations'])
  })

  it('shouldLeaveOverflowEmpty_whenRoleHasOneModule', () => {
    const { primary, overflow } = splitForBottomBar(visibleNavItems('VENTE'))

    expect(labels(primary)).toEqual(['Accueil', 'Ventes'])
    expect(overflow).toEqual([])
  })
})
