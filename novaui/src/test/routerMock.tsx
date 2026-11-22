import type { ReactNode } from 'react'
import { MemoryRouter } from 'react-router-dom'

export const navigateMock = vi.fn()

export const routerWrapper =
  (path = '/') =>
  ({ children }: { children: ReactNode }) => <MemoryRouter initialEntries={[path]}>{children}</MemoryRouter>
