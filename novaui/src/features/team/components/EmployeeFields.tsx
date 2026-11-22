import { Input } from '../../../components/ui/input'
import type { EmployeeFormErrors, EmployeeFormValues } from '../services/employeeForm'
import type { FieldChange } from '../types/formChange'

interface EmployeeFieldsProps {
  values: EmployeeFormValues
  errors: EmployeeFormErrors
  today: string
  onChange: FieldChange<EmployeeFormValues>
}

export const EmployeeFields = ({ values, errors, today, onChange }: EmployeeFieldsProps) => (
  <>
    <div className="grid gap-4 sm:grid-cols-2">
      <Input label="Prénom" required maxLength={100} autoComplete="given-name" value={values.firstName}
        invalid={errors.firstName !== undefined} helper={errors.firstName}
        onChange={(event) => onChange('firstName', event.target.value)} />
      <Input label="Nom" required maxLength={100} autoComplete="family-name" value={values.lastName}
        invalid={errors.lastName !== undefined} helper={errors.lastName}
        onChange={(event) => onChange('lastName', event.target.value)} />
    </div>
    <Input label="E-mail" type="email" required maxLength={255} autoComplete="email" placeholder="hery@boutique.mg"
      value={values.email} invalid={errors.email !== undefined} helper={errors.email}
      onChange={(event) => onChange('email', event.target.value)} />
    <Input label="Poste" maxLength={100} placeholder="Chauffeur" value={values.position}
      onChange={(event) => onChange('position', event.target.value)} />
    <div className="grid gap-4 sm:grid-cols-2">
      <Input label="Date d’arrivée" type="date" required max={today} value={values.hireDate}
        invalid={errors.hireDate !== undefined} helper={errors.hireDate}
        onChange={(event) => onChange('hireDate', event.target.value)} />
      <Input label="Salaire de base (Ar)" inputMode="decimal" required placeholder="800 000" value={values.baseSalary}
        invalid={errors.baseSalary !== undefined} helper={errors.baseSalary}
        onChange={(event) => onChange('baseSalary', event.target.value)} />
    </div>
  </>
)
