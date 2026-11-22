import { Input } from '../../../components/ui/input'
import { Textarea } from '../../../components/ui/textarea'
import { FormDialog } from '../components/FormDialog'
import { useNewLeave } from '../hooks/useNewLeave'
import { fullName } from '../services/teamFormat'
import type { Employee } from '../types/employee'

interface LeavePanelProps {
  employee: Employee
  onClose: () => void
}

export const LeavePanel = ({ employee, onClose }: LeavePanelProps) => {
  const { values, errors, update, submit, saving } = useNewLeave(employee.id, onClose)

  return (
    <FormDialog
      formId="demander-conge"
      title="Demander un congé"
      description={`Pour ${fullName(employee)}. La demande reste à valider par l’équipe RH.`}
      submitLabel="Envoyer la demande"
      saving={saving}
      onSubmit={submit}
      onClose={onClose}
    >
      <div className="grid gap-4 sm:grid-cols-2">
        <Input label="Du" type="date" required value={values.startDate} invalid={errors.startDate !== undefined}
          helper={errors.startDate} onChange={(event) => update('startDate', event.target.value)} />
        <Input label="Au" type="date" required min={values.startDate} value={values.endDate}
          invalid={errors.endDate !== undefined} helper={errors.endDate}
          onChange={(event) => update('endDate', event.target.value)} />
      </div>
      <Textarea label="Motif" rows={3} maxLength={500} value={values.reason}
        onChange={(event) => update('reason', event.target.value)} />
    </FormDialog>
  )
}
