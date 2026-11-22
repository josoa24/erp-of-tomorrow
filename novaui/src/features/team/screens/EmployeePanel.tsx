import { ContractChoice } from '../components/ContractChoice'
import { DepartmentSelect } from '../components/DepartmentSelect'
import { EmployeeFields } from '../components/EmployeeFields'
import { FormDialog } from '../components/FormDialog'
import { useNewEmployee } from '../hooks/useNewEmployee'

export const EmployeePanel = ({ onClose }: { onClose: () => void }) => {
  const { values, errors, update, today, departments, submit, saving } = useNewEmployee(onClose)

  return (
    <FormDialog
      formId="ajouter-employe"
      title="Ajouter un employé"
      description="La fiche est créée dans le service choisi."
      submitLabel="Ajouter l’employé"
      saving={saving}
      onSubmit={submit}
      onClose={onClose}
    >
      <EmployeeFields values={values} errors={errors} today={today} onChange={update} />
      <DepartmentSelect
        departments={departments}
        value={values.departmentId}
        error={errors.departmentId}
        onChange={(value) => update('departmentId', value)}
      />
      <ContractChoice value={values.contractType} onChange={(value) => update('contractType', value)} />
    </FormDialog>
  )
}
