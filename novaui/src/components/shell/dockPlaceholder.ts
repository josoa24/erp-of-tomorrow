import type { StaticDataRouteOption } from '@tanstack/react-router'
import { ECLIPSE_PLACEHOLDERS } from '../../constants/eclipsePlaceholders'

export const dockPlaceholderFor = (staticData?: StaticDataRouteOption): string =>
  staticData?.eclipsePlaceholder ?? ECLIPSE_PLACEHOLDERS.default

export const isDockHiddenFor = (staticData?: StaticDataRouteOption): boolean => staticData?.hideEclipseDock === true
