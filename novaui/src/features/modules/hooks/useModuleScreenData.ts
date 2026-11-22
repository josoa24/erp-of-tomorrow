import { getModuleDefinition } from '../api/moduleDefinitionsApi'
import { getIndicators, getRecords, getSalesImport, getStatusCounts } from '../api/moduleRecordsApi'
import type { ModuleDefinition } from '../types/moduleDefinition'
import { useModuleQuery } from './useModuleQuery'

export const hasSaleReference = (definition: ModuleDefinition | undefined): boolean =>
  definition?.columns.some((column) => column.reference === 'SALE') ?? false

export const useModuleDefinition = (slug: string) =>
  useModuleQuery(['definition', slug], (signal) => getModuleDefinition(slug, { signal }))

export const useModuleRecords = (slug: string, search: string, status: string | undefined) =>
  useModuleQuery(['records', slug, search, status ?? ''], (signal) => getRecords(slug, { search, status }, { signal }))

export const useModuleIndicators = (slug: string) =>
  useModuleQuery(['indicators', slug], (signal) => getIndicators(slug, { signal }))

export const useModuleStatusCounts = (slug: string) =>
  useModuleQuery(['status-counts', slug], (signal) => getStatusCounts(slug, { signal }))

export const useSalesImportAvailable = (slug: string, enabled: boolean) =>
  useModuleQuery(['sales-import', slug], (signal) => getSalesImport(slug, { signal }), enabled)
