import { TEAM } from '../../__tests__/teamFixtures'
import { visibleEmployees } from '../teamFilters'

const names = (filter: Parameters<typeof visibleEmployees>[1]) =>
  visibleEmployees(TEAM, filter).map((employee) => employee.firstName)

describe('visibleEmployees', () => {
  it('shouldSortByServiceThenName_whenShowingEveryone', () => {
    expect(names('tous')).toEqual(['Toky', 'Haja', 'Nirina', 'Fara'])
  })

  it('shouldKeepPresentEmployees', () => {
    expect(names('presents')).toEqual(['Toky', 'Haja', 'Fara'])
  })

  it('shouldKeepEmployeesOnLeave', () => {
    expect(names('conges')).toEqual(['Nirina'])
  })

  it('shouldKeepEmployeesWithPendingAdvance', () => {
    expect(names('avances')).toEqual(['Nirina'])
  })

  it('shouldNotMutateInput', () => {
    const before = TEAM.map((employee) => employee.id)

    visibleEmployees(TEAM, 'tous')

    expect(TEAM.map((employee) => employee.id)).toEqual(before)
  })
})
