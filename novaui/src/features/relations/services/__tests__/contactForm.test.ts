import { EMPTY_CONTACT_FORM, hasErrors, toNewContactRequest, validateContactForm } from '../contactForm'

const filled = { ...EMPTY_CONTACT_FORM, name: ' Épicerie Rova ', phone: '034 12 345 67', city: 'Antananarivo' }

describe('validateContactForm', () => {
  it('shouldRequireName', () => {
    expect(validateContactForm(EMPTY_CONTACT_FORM, false)).toHaveProperty('name')
  })

  it('shouldRejectMalformedEmail', () => {
    expect(validateContactForm({ ...filled, email: 'rova@' }, false)).toHaveProperty('email')
  })

  it('shouldRejectNonNumericCreditLimit_whenAdapted', () => {
    expect(validateContactForm({ ...filled, creditLimit: '2 M' }, true)).toHaveProperty('creditLimit')
  })

  it('shouldIgnoreCreditLimit_withoutAdaptation', () => {
    expect(hasErrors(validateContactForm({ ...filled, creditLimit: 'abc' }, false))).toBe(false)
  })

  it('shouldAcceptSpacedCreditLimit', () => {
    expect(hasErrors(validateContactForm({ ...filled, creditLimit: '2 000 000' }, true))).toBe(false)
  })
})

describe('toNewContactRequest', () => {
  it('shouldSendGenericCustomerFieldsOnly_withoutAdaptation', () => {
    expect(toNewContactRequest({ ...filled, nif: '4000123456', creditLimit: '5' }, false)).toEqual({
      kind: 'CUSTOMER',
      name: 'Épicerie Rova',
      phone: '034 12 345 67',
      email: undefined,
      city: 'Antananarivo',
      type: 'PARTICULIER',
    })
  })

  it('shouldSendFiscalIdsAndCreditLimit_forAdaptedProfessionalCustomer', () => {
    const values = { ...filled, category: 'ENTREPRISE' as const, nif: '4000123456', stat: '', creditLimit: '2 000 000' }

    expect(toNewContactRequest(values, true)).toMatchObject({
      type: 'ENTREPRISE',
      nif: '4000123456',
      stat: undefined,
      creditLimit: 2_000_000,
    })
  })

  it('shouldSkipFiscalIds_forPrivateCustomer', () => {
    expect(toNewContactRequest({ ...filled, nif: '4000123456' }, true)).not.toHaveProperty('nif')
  })

  it('shouldSendFiscalIdsButNoCustomerFields_forSupplier', () => {
    const request = toNewContactRequest({ ...filled, kind: 'SUPPLIER', nif: '1000234567', creditLimit: '9' }, true)

    expect(request).toMatchObject({ kind: 'SUPPLIER', nif: '1000234567' })
    expect(request).not.toHaveProperty('type')
    expect(request).not.toHaveProperty('creditLimit')
  })
})
