import { useId } from 'react'
import { Button } from '../../../components/ui/button'
import { Dialog } from '../../../components/ui/dialog'
import { Input } from '../../../components/ui/input'
import { FormDialog } from '../../team/components/FormDialog'
import { useCreateUserForm } from '../hooks/useCreateUserForm'
import { GeneratedPasswordField } from './GeneratedPasswordField'
import { PasswordRevealPanel } from './PasswordRevealPanel'
import { RoleCombobox } from './RoleCombobox'

export const CreateUserDialog = ({ onClose }: { onClose: () => void }) => {
  const formId = useId()
  const { values, errors, update, generated, created, submit, saving } = useCreateUserForm()

  if (created) {
    return (
      <Dialog open onClose={onClose} title="Compte créé" description="Le nouvel utilisateur peut se connecter dès maintenant."
        footer={<Button onClick={onClose}>Terminé</Button>}>
        <PasswordRevealPanel account={created.email} password={created.password} />
      </Dialog>
    )
  }

  return (
    <FormDialog formId={formId} title="Nouvel utilisateur" description="Créez un compte et choisissez son rôle."
      submitLabel="Créer le compte" saving={saving} onSubmit={submit} onClose={onClose}>
      <Input label="Nom complet" maxLength={120} autoComplete="off" placeholder="Hery Rakoto" value={values.fullName}
        onChange={(event) => update('fullName', event.target.value)} />
      <Input label="Adresse e-mail" type="email" required maxLength={255} autoComplete="off" placeholder="hery@boutique.mg"
        value={values.email} invalid={errors.email !== undefined} helper={errors.email}
        onChange={(event) => update('email', event.target.value)} />
      <RoleCombobox value={values.role} error={errors.role} onChange={(role) => update('role', role)} />
      <GeneratedPasswordField value={generated.password} visible={generated.visible} error={errors.password}
        onChange={generated.setPassword} onToggleVisible={generated.toggleVisible} onGenerate={generated.generate} />
    </FormDialog>
  )
}
