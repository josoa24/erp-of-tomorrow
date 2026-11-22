import { useNavigate, useSearch } from '@tanstack/react-router'
import { useCallback, useMemo, useState } from 'react'
import {
  belongsToModule,
  resolvePreview,
  withPreview,
  withoutPreview,
  type StoredPreviews,
} from '../services/adaptationPreview'
import type { AdaptationModuleKey, AdaptationProposal } from '../types/adaptation'
import type { EclipseAdaptationContextValue } from './eclipse-adaptation-context'

export const useAdaptationPreviewState = (): EclipseAdaptationContextValue => {
  const navigate = useNavigate()
  const searchKey = useSearch({ strict: false, select: (search) => search.eclipse })
  const [stored, setStored] = useState<StoredPreviews>({})

  const startPreview = useCallback(
    (proposal: AdaptationProposal) => {
      setStored((previous) => withPreview(previous, proposal))
      void navigate({ to: '.', search: (previous) => ({ ...previous, eclipse: proposal.adaptationKey }) })
    },
    [navigate],
  )

  const clearPreview = useCallback(
    (moduleKey: AdaptationModuleKey) => {
      setStored((previous) => withoutPreview(previous, moduleKey))
      if (belongsToModule(searchKey, moduleKey)) {
        void navigate({ to: '.', search: (previous) => ({ ...previous, eclipse: undefined }) })
      }
    },
    [navigate, searchKey],
  )

  const preview = useMemo(() => resolvePreview(searchKey, stored), [searchKey, stored])

  return useMemo(() => ({ preview, startPreview, clearPreview }), [preview, startPreview, clearPreview])
}
