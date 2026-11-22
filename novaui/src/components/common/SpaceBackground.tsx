import type { ReactNode } from 'react'
import { cn } from '@/lib/utils'
import { generateStars } from '../../utils/stars'

const STARS = generateStars(34, 7)

type SpaceVariant = 'login' | 'dashboard'

const BACKGROUNDS: Record<SpaceVariant, string> = {
  login: 'bg-space-login',
  dashboard: 'bg-space-dashboard',
}

const GLOW = 'absolute h-[30vh] w-[46vw] rounded-[50%] blur-[70px]'

interface SpaceBackgroundProps {
  variant?: SpaceVariant
  children: ReactNode
}

export const SpaceBackground = ({ variant = 'dashboard', children }: SpaceBackgroundProps) => {
  const glowOpacity = variant === 'login' ? 'opacity-55' : 'opacity-25'

  return (
    <div className={cn('relative min-h-screen overflow-hidden', BACKGROUNDS[variant])}>
      <div className="pointer-events-none absolute inset-0" aria-hidden="true">
        {STARS.map((star, index) => (
          <span
            key={index}
            className="absolute rounded-full bg-[#c7d2ff] shadow-[0_0_6px_rgba(160,180,255,0.9)]"
            style={{
              left: `${star.x}%`,
              top: `${star.y}%`,
              width: star.size,
              height: star.size,
              opacity: star.opacity,
            }}
          />
        ))}
        {variant === 'login' && (
          <div className="absolute top-26 left-1/2 aspect-square w-[min(880px,120vw)] -translate-x-1/2 rounded-full shadow-arc mask-[linear-gradient(180deg,#000_0%,#000_6%,transparent_24%)]" />
        )}
        <div className={cn(GLOW, glowOpacity, '-left-[14vw] bottom-[4vh] rotate-18 bg-[#4b22b8]')} />
        <div className={cn(GLOW, glowOpacity, '-right-[14vw] bottom-[2vh] -rotate-22 bg-[#3a2ab8]')} />
      </div>
      <div className="relative z-1 min-h-screen">{children}</div>
    </div>
  )
}
