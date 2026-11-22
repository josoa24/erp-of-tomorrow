import { cn } from '@/lib/utils'
import logotypeUrl from '../../assets/nova-logotype.svg'
import symbolUrl from '../../assets/nova-symbol.svg'
import { APP_NAME } from '../../constants/appInfo'

export type NovaLogoSize = 's' | 'm' | 'l'

export interface NovaLogoProps {
  size?: NovaLogoSize
  symbolOnly?: boolean
  className?: string
}

const HEIGHTS: Record<NovaLogoSize, number> = { s: 23, m: 40, l: 48 }

const LOGOTYPE_RATIO = 154.502 / 40
const SYMBOL_RATIO = 54.0899 / 40

export const NovaLogo = ({ size = 's', symbolOnly = false, className }: NovaLogoProps) => {
  const height = HEIGHTS[size]
  const ratio = symbolOnly ? SYMBOL_RATIO : LOGOTYPE_RATIO

  return (
    <img
      src={symbolOnly ? symbolUrl : logotypeUrl}
      alt={APP_NAME}
      width={Math.round(height * ratio * 10) / 10}
      height={height}
      className={cn('block shrink-0 select-none', className)}
      draggable={false}
    />
  )
}
