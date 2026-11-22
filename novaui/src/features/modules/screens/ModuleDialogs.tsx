import { ConfirmDialog } from '../components/ConfirmDialog'
import { ModuleRecordDialog } from '../components/ModuleRecordDialog'
import { useAdvanceRecord } from '../hooks/useModuleActions'
import { useModuleDelete } from '../hooks/useModuleDelete'
import type { ModuleDialog } from '../hooks/useModuleDialogs'
import type { ModuleDefinition } from '../types/moduleDefinition'

interface ModuleDialogsProps {
  definition: ModuleDefinition
  dialog: ModuleDialog | null
  onClose: () => void
}

export const ModuleDialogs = ({ definition, dialog, onClose }: ModuleDialogsProps) => {
  const advance = useAdvanceRecord(definition.slug)
  const remove = useModuleDelete(definition.slug, definition.name, onClose)
  if (dialog === null) return null
  if (dialog.type === 'create') return <ModuleRecordDialog definition={definition} onClose={onClose} />
  if (dialog.type === 'delete') {
    return (
      <ConfirmDialog
        title={`Supprimer le module « ${definition.name} » ?`}
        description="Le module disparaît de Mes modules. Il doit être vide pour être supprimé."
        confirmLabel="Supprimer le module"
        danger
        loading={remove.isPending}
        onConfirm={() => remove.mutate()}
        onClose={onClose}
      />
    )
  }
  return (
    <ConfirmDialog
      title={`Passer à « ${dialog.next.label} » ?`}
      description={dialog.next.final ? 'Ce statut est définitif : l’enregistrement ne pourra plus changer.' : 'Le statut sera mis à jour pour toute l’équipe.'}
      confirmLabel={`Passer à « ${dialog.next.label} »`}
      loading={advance.isPending}
      onConfirm={() => advance.mutate({ id: dialog.record.id, label: dialog.next.label }, { onSuccess: onClose })}
      onClose={onClose}
    />
  )
}
