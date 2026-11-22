import { StatusPill, type StatusTone as PillTone } from '../../../components/ui/status-pill'
import type { ModuleStatus, StatusTone } from '../types/moduleDefinition'

const TONES: Record<StatusTone, PillTone> = {
  SUCCESS: 'success',
  WARNING: 'warning',
  CRITICAL: 'critical',
  INFO: 'info',
  NEUTRAL: 'neutral',
}

export const RecordStatusPill = ({ status }: { status: ModuleStatus | undefined }) =>
  status ? <StatusPill label={status.label} tone={TONES[status.tone]} /> : null
