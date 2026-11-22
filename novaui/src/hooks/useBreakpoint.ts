import { useMediaQuery } from './useMediaQuery'

export type Breakpoint = 'mobile' | 'tablet' | 'desktop'

export const TABLET_QUERY = '(min-width: 768px)'
export const DESKTOP_QUERY = '(min-width: 1280px)'

export const useBreakpoint = (): Breakpoint => {
  const atLeastTablet = useMediaQuery(TABLET_QUERY, true)
  const atLeastDesktop = useMediaQuery(DESKTOP_QUERY, true)
  if (atLeastDesktop) return 'desktop'
  return atLeastTablet ? 'tablet' : 'mobile'
}
