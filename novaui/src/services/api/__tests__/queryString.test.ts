import { buildQuery } from '../queryString'

describe('buildQuery', () => {
  it('shouldReturnEmptyString_whenNoParamIsSet', () => {
    expect(buildQuery({ search: '', page: undefined, sort: null })).toBe('')
  })

  it('shouldEncodeScalarsAndSkipEmptyValues', () => {
    expect(buildQuery({ search: 'riz blanc', page: 2, actif: true, vide: '' })).toBe(
      '?search=riz+blanc&page=2&actif=true',
    )
  })

  it('shouldRepeatKey_whenValueIsAList', () => {
    expect(buildQuery({ status: ['WARNING', 'CRITICAL'] })).toBe('?status=WARNING&status=CRITICAL')
  })
})
