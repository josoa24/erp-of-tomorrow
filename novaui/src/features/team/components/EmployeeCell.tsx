import { Avatar } from '../../../components/ui/avatar'
import { fullName } from '../services/teamFormat'
import type { Employee } from '../types/employee'

export const EmployeeCell = ({ employee }: { employee: Employee }) => {
  const name = fullName(employee)
  return (
    <span className="flex min-w-0 items-center gap-2.5">
      <Avatar name={name} tone="lilac" decorative />
      <span className="flex min-w-0 flex-col whitespace-normal">
        <span className="text-label break-words text-ink-primary">{name}</span>
        <span className="text-micro text-ink-tertiary">{employee.position ?? 'Poste non précisé'}</span>
      </span>
    </span>
  )
}
