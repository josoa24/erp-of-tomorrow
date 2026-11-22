import { Info } from 'lucide-react'
import { StatusPill } from '../../../components/ui/status-pill'
import type { EmployeeStatus } from '../types/employee'

export const EmployeeStatusPill = ({ status }: { status: EmployeeStatus }) =>
  status === 'ON_LEAVE' ? <StatusPill label="En congé" tone="info" icon={Info} /> : <StatusPill label="Présent" tone="success" />
