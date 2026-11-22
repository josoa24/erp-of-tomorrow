import { renderHook } from '@testing-library/react'
import { useBodyScrollLock } from '../useBodyScrollLock'

describe('useBodyScrollLock', () => {
  it('shouldLockBodyScroll_whileActive_andRestoreAfter', () => {
    const { rerender } = renderHook(({ active }) => useBodyScrollLock(active), { initialProps: { active: true } })

    expect(document.body.style.overflow).toBe('hidden')
    rerender({ active: false })

    expect(document.body.style.overflow).toBe('')
  })
})
