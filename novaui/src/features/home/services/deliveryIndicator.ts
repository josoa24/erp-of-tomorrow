import type { RequestOptions } from '../../../services/api/httpClient'
import { getModuleIndicators, getModuleSummaries, getModuleDefinition } from '../api/homeApi'
import type { DeliveryIndicator } from '../types/home'

const hasRatio = async (slug: string, options: RequestOptions): Promise<boolean> => {
  const definition = await getModuleDefinition(slug, options)
  return definition.indicators.some((indicator) => indicator.kind === 'RATIO_TODAY')
}

export const loadDeliveryIndicator = async (options: RequestOptions = {}): Promise<DeliveryIndicator | null> => {
  const modules = await getModuleSummaries(options)
  for (const module of modules) {
    if (!(await hasRatio(module.slug, options))) continue
    const indicators = await getModuleIndicators(module.slug, options)
    const ratio = indicators.find((indicator) => indicator.kind === 'RATIO_TODAY')
    const count = indicators.find((indicator) => indicator.kind === 'COUNT')
    if (!ratio) continue
    return {
      moduleName: module.name,
      delivered: ratio.value,
      total: ratio.total ?? ratio.value,
      inProgress: count ? { label: count.label, value: count.value } : null,
    }
  }
  return null
}
