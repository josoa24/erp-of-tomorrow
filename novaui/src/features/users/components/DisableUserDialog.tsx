import { Button } from '../../../components/ui/button'
import { Dialog } from '../../../components/ui/dialog'
import { useDisableUser } from '../hooks/useUserCommands'
import { userRowLabel } from '../services/usersFormat'
import type { ManagedUser } from '../types/user'

export const DISABLE_EXPLANATION =
  'Le compte ne pourra plus se connecter. Une session déjà ouverte reste valable jusqu’à son expiration.'

interface DisableUserDialogProps {
  user: ManagedUser
  onClose: () => void
}

export const DisableUserDialog = ({ user, onClose }: DisableUserDialogProps) => {
  const { mutate, isPending } = useDisableUser()

  return (
    <Dialog
      open
      onClose={onClose}
      title={`Désactiver ${userRowLabel(user)}`}
      description={DISABLE_EXPLANATION}
      footer={
        <>
          <Button variant="secondary" onClick={onClose} disabled={isPending}>Annuler</Button>
          <Button variant="danger" loading={isPending} onClick={() => mutate(user, { onSuccess: onClose })}>Désactiver le compte</Button>
        </>
      }
    >
      <p className="text-body text-ink-secondary">{user.email}</p>
    </Dialog>
  )
}
