import type { ReactNode } from 'react'
import { NovaBackground } from '../../../components/brand/NovaBackground'
import { NovaLogo } from '../../../components/brand/NovaLogo'

export const ErrorLayout = ({ children }: { children: ReactNode }) => (
  <>
    <NovaBackground />
    <NovaLogo size="m" className="absolute left-4 top-6 sm:left-8 sm:top-8" />
    <main className="mx-auto flex min-h-dvh w-full max-w-[592px] flex-col items-center justify-center px-4 py-24">
      {children}
    </main>
  </>
)
