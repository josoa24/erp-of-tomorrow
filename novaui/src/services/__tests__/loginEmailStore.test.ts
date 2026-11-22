import { forgetLoginEmail, readLoginEmail, rememberLoginEmail } from '../loginEmailStore'

describe('loginEmailStore', () => {
  afterEach(() => forgetLoginEmail())

  it('shouldReturnRememberedEmail', () => {
    rememberLoginEmail('hery@boutique-tana.mg')

    expect(readLoginEmail()).toBe('hery@boutique-tana.mg')
  })

  it('shouldReturnEmpty_whenForgotten', () => {
    rememberLoginEmail('hery@boutique-tana.mg')

    forgetLoginEmail()

    expect(readLoginEmail()).toBe('')
  })

  it('shouldReturnEmpty_whenStorageIsBlocked', () => {
    const getItem = vi.spyOn(Storage.prototype, 'getItem').mockImplementation(() => {
      throw new Error('bloqué')
    })

    expect(readLoginEmail()).toBe('')
    getItem.mockRestore()
  })
})
