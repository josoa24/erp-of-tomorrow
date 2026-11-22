import { cn } from '@/lib/utils'

interface ConnectorProps {
  side: 'left' | 'right'
  direction: 'down' | 'up'
}

const PATHS = {
  down: 'M0 50 H62 C96 50 118 68 186 82',
  up: 'M0 50 H62 C96 50 118 32 186 18',
}

export const Connector = ({ side, direction }: ConnectorProps) => (
  <svg
    className={cn('relative z-0 block overflow-visible', side === 'right' && '-ml-15 -scale-x-100')}
    width="190"
    height="100"
    viewBox="0 0 190 100"
    aria-hidden="true"
  >
    <path d={PATHS[direction]} className="fill-none stroke-[rgba(140,160,255,0.65)] stroke-[1.5] drop-shadow-[0_0_3px_rgba(130,150,255,0.8)]" />
    <circle cx="66" cy="50" r="4.5" className="fill-[#a7bcff] drop-shadow-[0_0_5px_rgba(150,175,255,1)]" />
  </svg>
)
