import { canAccessModule } from '../roleAccess'

describe('canAccessModule', () => {
  it('shouldAllowEveryModule_whenAdmin', () => {
    expect(['stock', 'vente', 'rh', 'crm'].every((m) => canAccessModule('ADMIN', m as never))).toBe(true)
  })

  it('shouldAllowOwnModule_whenBusinessRole', () => {
    expect(canAccessModule('STOCK', 'stock')).toBe(true)
  })

  it('shouldRefuseOtherModule_whenBusinessRole', () => {
    expect(canAccessModule('RH', 'vente')).toBe(false)
  })
})
