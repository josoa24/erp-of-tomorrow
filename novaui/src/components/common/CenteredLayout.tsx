import type { ReactNode } from 'react'
import { EclipseLogo } from './EclipseLogo'
import { SpaceBackground } from './SpaceBackground'

interface CenteredLayoutProps {
  children: ReactNode
}

export const CenteredLayout = ({ children }: CenteredLayoutProps) => (
  <SpaceBackground variant="login">
    <header className="absolute top-8 left-13 max-sm:top-6 max-sm:left-5">
      <EclipseLogo />
    </header>
    <main className="grid min-h-screen place-items-center px-4 pt-30 pb-15">{children}</main>
  </SpaceBackground>
)
