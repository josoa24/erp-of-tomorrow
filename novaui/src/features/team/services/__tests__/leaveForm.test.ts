import { EMPTY_LEAVE_FORM, toLeaveRequest, validateLeaveForm } from '../leaveForm'

describe('leaveForm', () => {
  it('shouldRequireBothDates', () => {
    expect(validateLeaveForm(EMPTY_LEAVE_FORM)).toEqual({ startDate: 'Champ obligatoire', endDate: 'Champ obligatoire' })
  })

  it('shouldRefuseEndBeforeStart', () => {
    const errors = validateLeaveForm({ startDate: '2026-12-27', endDate: '2026-12-23', reason: '' })

    expect(errors.endDate).toMatch(/suivre/)
  })

  it('shouldAcceptSingleDay', () => {
    expect(validateLeaveForm({ startDate: '2026-12-23', endDate: '2026-12-23', reason: '' })).toEqual({
      startDate: undefined,
      endDate: undefined,
    })
  })

  it('shouldBuildRequest', () => {
    expect(toLeaveRequest(3, { startDate: '2026-12-23', endDate: '2026-12-27', reason: ' ' })).toEqual({
      employeeId: 3,
      startDate: '2026-12-23',
      endDate: '2026-12-27',
      reason: undefined,
    })
  })
})
