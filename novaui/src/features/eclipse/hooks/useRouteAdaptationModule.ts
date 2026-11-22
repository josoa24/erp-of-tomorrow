import { useMatches } from '@tanstack/react-router'
import type { AdaptationModuleKey } from '../types/adaptation'

export const useRouteAdaptationModule = (): AdaptationModuleKey | null =>
  useMatches({ select: (matches) => matches[matches.length - 1]?.staticData.adaptationModule ?? null })
