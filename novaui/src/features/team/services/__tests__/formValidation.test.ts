import { hasErrors, isEmail, optionalText, parseAmount } from '../formValidation'

describe('formValidation', () => {
  it.each([
    ['150 000', 150000],
    ['150 000', 150000],
    ['1250,5', 1250.5],
    ['', null],
    ['abc', null],
  ])('shouldParseAmount_%s', (value, expected) => {
    expect(parseAmount(value)).toBe(expected)
  })

  it('shouldRecognizeEmail', () => {
    expect(isEmail('rasoa.andria@boutique-tana.mg')).toBe(true)
    expect(isEmail('rasoa@')).toBe(false)
  })

  it('shouldDetectErrors', () => {
    expect(hasErrors({ a: undefined, b: 'Champ obligatoire' })).toBe(true)
    expect(hasErrors({ a: undefined })).toBe(false)
  })

  it('shouldTrimOptionalText_andDropBlank', () => {
    expect(optionalText('  Rentrée  ')).toBe('Rentrée')
    expect(optionalText('   ')).toBeUndefined()
  })
})
