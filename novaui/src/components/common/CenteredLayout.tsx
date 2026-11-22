import type { ReactNode } from 'react'
import { NovaBackground } from '../brand/NovaBackground'
import { NovaLogo } from '../brand/NovaLogo'

interface CenteredLayoutProps {
  children: ReactNode
}

export const CenteredLayout = ({ children }: CenteredLayoutProps) => (
  <>
    <NovaBackground />
    <main className="mx-auto flex min-h-screen w-full max-w-[440px] flex-col items-center justify-center gap-8 px-4 py-16">
      <NovaLogo size="l" className="max-sm:h-10 max-sm:w-auto" />
      {children}
    </main>
  </>
)
