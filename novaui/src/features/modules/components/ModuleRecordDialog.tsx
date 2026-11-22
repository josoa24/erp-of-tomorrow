import { Button } from '../../../components/ui/button'
import { Dialog } from '../../../components/ui/dialog'
import { useBreakpoint } from '../../../hooks/useBreakpoint'
import { useCreateRecord } from '../hooks/useModuleActions'
import { useRecordForm } from '../hooks/useRecordForm'
import { newRecordLabel } from '../services/moduleLabels'
import type { ModuleDefinition } from '../types/moduleDefinition'
import { RecordField } from './RecordField'

const FORM_ID = 'nouvel-enregistrement'

interface ModuleRecordDialogProps {
  definition: ModuleDefinition
  onClose: () => void
}

export const ModuleRecordDialog = ({ definition, onClose }: ModuleRecordDialogProps) => {
  const { values, errors, update, prepare } = useRecordForm(definition.columns)
  const { mutate, isPending } = useCreateRecord(definition.slug)
  const title = newRecordLabel(definition.name)
  const placement = useBreakpoint() === 'mobile' ? 'sheet' : 'center'

  const submit = () => {
    const recordValues = prepare()
    if (recordValues) mutate(recordValues, { onSuccess: onClose })
  }

  return (
    <Dialog
      open
      onClose={onClose}
      title={title}
      placement={placement}
      description={`L’enregistrement démarre au statut « ${definition.statuses[0]?.label ?? ''} ».`}
      footer={
        <>
          <Button variant="secondary" onClick={onClose} disabled={isPending}>Annuler</Button>
          <Button type="submit" form={FORM_ID} loading={isPending}>Enregistrer</Button>
        </>
      }
    >
      <form
        id={FORM_ID}
        noValidate
        className="flex flex-col gap-5"
        onSubmit={(event) => {
          event.preventDefault()
          submit()
        }}
      >
        {definition.columns.map((column) => (
          <RecordField
            key={column.key}
            column={column}
            value={values[column.key] ?? null}
            error={errors[column.key]}
            onChange={(value) => update(column.key, value)}
          />
        ))}
      </form>
    </Dialog>
  )
}
