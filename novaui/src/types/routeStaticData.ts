import type { AdaptationModuleKey } from '../features/eclipse/types/adaptation'

declare module '@tanstack/router-core' {
  interface StaticDataRouteOption {
    eclipsePlaceholder?: string
    hideEclipseDock?: boolean
    adaptationModule?: AdaptationModuleKey
  }
}

export {}
