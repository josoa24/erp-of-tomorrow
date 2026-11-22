import { useId, useState } from 'react'
import { ROLE_LABELS } from '../../../constants/roleLabels'
import type { Role } from '../../../types/auth'
import { FormDialog } from '../../team/components/FormDialog'
import { useChangeRole } from '../hooks/useUserCommands'
import { userDisplayName } from '../services/usersFormat'
import type { ManagedUser } from '../types/user'
import { RoleCombobox } from './RoleCombobox'

interface ChangeRoleDialogProps {
  user: ManagedUser
  onClose: () => void
}

export const ChangeRoleDialog = ({ user, onClose }: ChangeRoleDialogProps) => {
  const formId = useId()
  const [role, setRole] = useState<Role | null>(user.role)
  const { mutate, isPending } = useChangeRole()
  const unchanged = role === null || role === user.role

  return (
    <FormDialog formId={formId} title="Changer le rôle" saving={isPending} submitDisabled={unchanged} submitLabel="Enregistrer" onClose={onClose}
      description={`${userDisplayName(user)} · rôle actuel : ${ROLE_LABELS[user.role]}`}
      onSubmit={() => role && !unchanged && mutate({ user, role }, { onSuccess: onClose })}>
      <RoleCombobox value={role} onChange={setRole} />
    </FormDialog>
  )
}
