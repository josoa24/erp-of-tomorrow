import type { UsersDialog } from '../hooks/useUsersDialogs'
import { ChangeRoleDialog } from './ChangeRoleDialog'
import { CreateUserDialog } from './CreateUserDialog'
import { DisableUserDialog } from './DisableUserDialog'
import { ResetPasswordDialog } from './ResetPasswordDialog'

interface UsersDialogsProps {
  dialog: UsersDialog
  onClose: () => void
}

export const UsersDialogs = ({ dialog, onClose }: UsersDialogsProps) => {
  if (dialog?.kind === 'create') return <CreateUserDialog onClose={onClose} />
  if (dialog?.kind === 'role') return <ChangeRoleDialog key={dialog.user.id} user={dialog.user} onClose={onClose} />
  if (dialog?.kind === 'password') return <ResetPasswordDialog key={dialog.user.id} user={dialog.user} onClose={onClose} />
  if (dialog?.kind === 'disable') return <DisableUserDialog user={dialog.user} onClose={onClose} />
  return null
}
