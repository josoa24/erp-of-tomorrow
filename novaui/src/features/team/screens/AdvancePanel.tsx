import { Input } from '../../../components/ui/input'
import { Textarea } from '../../../components/ui/textarea'
import { FormDialog } from '../components/FormDialog'
import { useNewAdvance } from '../hooks/useNewAdvance'
import { capHelper } from '../services/advanceForm'
import { fullName } from '../services/teamFormat'
import type { Employee } from '../types/employee'

interface AdvancePanelProps {
  employee: Employee
  onClose: () => void
}

export const AdvancePanel = ({ employee, onClose }: AdvancePanelProps) => {
  const { values, errors, update, submit, saving } = useNewAdvance(employee.id, onClose)

  return (
    <FormDialog
      formId="demander-avance"
      title="Demander une avance"
      description={`Pour ${fullName(employee)}. L’avance est validée par l’administrateur.`}
      submitLabel="Demander l’avance"
      saving={saving}
      onSubmit={submit}
      onClose={onClose}
    >
      <Input label="Montant (Ar)" inputMode="decimal" required placeholder="150 000" value={values.amount}
        invalid={errors.amount !== undefined} helper={errors.amount ?? capHelper(employee, values.amount)}
        onChange={(event) => update('amount', event.target.value)} />
      <Textarea label="Motif" rows={3} maxLength={500} value={values.reason}
        onChange={(event) => update('reason', event.target.value)} />
    </FormDialog>
  )
}
