import { advanceRecord, createRecord, runSalesImport } from '../api/moduleRecordsApi'
import { useModuleMutation } from './useModuleMutation'

export const useCreateRecord = (slug: string) =>
  useModuleMutation(
    (values: Record<string, unknown>) => createRecord(slug, values),
    () => 'Enregistrement ajouté',
  )

export const useAdvanceRecord = (slug: string) =>
  useModuleMutation(
    ({ id }: { id: number; label: string }) => advanceRecord(slug, id),
    (_, { label }) => `Passé à « ${label} »`,
  )

export const useSalesImport = (slug: string) =>
  useModuleMutation(
    () => runSalesImport(slug),
    ({ created }) => `${created} ${created > 1 ? 'ventes reprises' : 'vente reprise'}`,
  )
