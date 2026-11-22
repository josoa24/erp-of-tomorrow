import type { ReactNode } from 'react'
import { NovaBackground } from '../brand/NovaBackground'
import { NovaLogo } from '../brand/NovaLogo'

interface CenteredLayoutProps {
  tagline?: string
  children: ReactNode
}

export const CenteredLayout = ({ tagline, children }: CenteredLayoutProps) => (
  <>
    <NovaBackground />
    <main className="mx-auto flex min-h-dvh w-full max-w-[472px] flex-col items-center justify-center gap-8 px-4 py-16">
      <div className="flex flex-col items-center gap-3 text-center">
        <NovaLogo size="l" className="max-sm:h-10 max-sm:w-auto" />
        {tagline && <p className="text-body text-ink-secondary">{tagline}</p>}
      </div>
      {children}
    </main>
  </>
)
