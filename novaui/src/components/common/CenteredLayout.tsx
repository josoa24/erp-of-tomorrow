import type { ReactNode } from 'react'
import { EclipseLogo } from './EclipseLogo'
import { SpaceBackground } from './SpaceBackground'
import styles from './CenteredLayout.module.css'

interface CenteredLayoutProps {
  children: ReactNode
}

export const CenteredLayout = ({ children }: CenteredLayoutProps) => (
  <SpaceBackground variant="login">
    <header className={styles.header}>
      <EclipseLogo />
    </header>
    <main className={styles.main}>{children}</main>
  </SpaceBackground>
)
