import { useId, useState } from 'react'
import { ROLE_LABELS } from '../../../constants/roleLabels'
import type { Role } from '../../../types/auth'
import { FormDialog } from '../../team/components/FormDialog'
import { sameRoles } from '../constants/roles'
import { useChangeRoles } from '../hooks/useUserCommands'
import { userRowLabel } from '../services/usersFormat'
import type { ManagedUser } from '../types/user'
import { RoleChoices } from './RoleChoices'

interface ChangeRoleDialogProps {
  user: ManagedUser
  onClose: () => void
}

export const ChangeRoleDialog = ({ user, onClose }: ChangeRoleDialogProps) => {
  const formId = useId()
  const [roles, setRoles] = useState<Role[]>(user.roles)
  const { mutate, isPending } = useChangeRoles()
  const unchanged = roles.length === 0 || sameRoles(roles, user.roles)

  return (
    <FormDialog formId={formId} title="Changer les rôles" saving={isPending} submitDisabled={unchanged} submitLabel="Enregistrer" onClose={onClose}
      description={`${userRowLabel(user)} · rôles actuels : ${user.roles.map((role) => ROLE_LABELS[role]).join(', ')}`}
      onSubmit={() => !unchanged && mutate({ user, roles }, { onSuccess: onClose })}>
      <RoleChoices value={roles} onChange={setRoles} />
    </FormDialog>
  )
}
