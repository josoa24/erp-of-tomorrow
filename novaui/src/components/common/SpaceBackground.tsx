import type { ReactNode } from 'react'
import { generateStars } from '../../utils/stars'
import styles from './SpaceBackground.module.css'

const STARS = generateStars(34, 7)

interface SpaceBackgroundProps {
  variant?: 'login' | 'dashboard'
  children: ReactNode
}

export const SpaceBackground = ({ variant = 'dashboard', children }: SpaceBackgroundProps) => (
  <div className={`${styles.page} ${styles[variant]}`}>
    <div className={styles.decor} aria-hidden="true">
      {STARS.map((star, index) => (
        <span
          key={index}
          className={styles.star}
          style={{
            left: `${star.x}%`,
            top: `${star.y}%`,
            width: star.size,
            height: star.size,
            opacity: star.opacity,
          }}
        />
      ))}
      {variant === 'login' && <div className={styles.arc} />}
      <div className={`${styles.glow} ${styles.glowLeft}`} />
      <div className={`${styles.glow} ${styles.glowRight}`} />
    </div>
    <div className={styles.content}>{children}</div>
  </div>
)
