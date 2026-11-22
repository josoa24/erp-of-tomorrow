import { useId, useState } from 'react'
import { Button } from '../../../components/ui/button'
import { Dialog } from '../../../components/ui/dialog'
import { FormDialog } from '../../team/components/FormDialog'
import { useFocusFirstInvalid } from '../hooks/useFocusFirstInvalid'
import { useGeneratedPassword } from '../hooks/useGeneratedPassword'
import { useResetPassword } from '../hooks/useUserCommands'
import { validatePassword } from '../services/userForms'
import { userDisplayName } from '../services/usersFormat'
import type { ManagedUser } from '../types/user'
import { GeneratedPasswordField } from './GeneratedPasswordField'
import { PasswordRevealPanel } from './PasswordRevealPanel'

interface ResetPasswordDialogProps {
  user: ManagedUser
  onClose: () => void
}

export const ResetPasswordDialog = ({ user, onClose }: ResetPasswordDialogProps) => {
  const formId = useId()
  const generated = useGeneratedPassword()
  const [submitted, setSubmitted] = useState(false)
  const [attempt, setAttempt] = useState(0)
  const [done, setDone] = useState<string | null>(null)
  const { mutate, reset, isPending } = useResetPassword()
  const error = submitted ? validatePassword(generated.password) : undefined
  useFocusFirstInvalid(formId, attempt)

  const submit = () => {
    setSubmitted(true)
    if (validatePassword(generated.password)) {
      setAttempt((count) => count + 1)
      return
    }
    const password = generated.password
    mutate({ user, password }, { onSuccess: () => {
      setDone(password)
      reset()
    } })
  }

  if (done) {
    return (
      <Dialog open onClose={onClose} title="Mot de passe réinitialisé" description={`Le compte de ${userDisplayName(user)} utilise ce nouveau mot de passe.`}
        footer={<Button onClick={onClose}>Terminé</Button>}>
        <PasswordRevealPanel account={user.email} password={done} />
      </Dialog>
    )
  }

  return (
    <FormDialog formId={formId} title="Réinitialiser le mot de passe" description={`${userDisplayName(user)} · ${user.email}`}
      submitLabel="Réinitialiser" saving={isPending} onSubmit={submit} onClose={onClose}>
      <GeneratedPasswordField label="Nouveau mot de passe" value={generated.password} visible={generated.visible} error={error}
        onChange={generated.setPassword} onToggleVisible={generated.toggleVisible} onGenerate={generated.generate} />
    </FormDialog>
  )
}
