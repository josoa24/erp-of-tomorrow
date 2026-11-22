import { Button } from '../../../components/ui/button'
import { Dialog } from '../../../components/ui/dialog'
import { Input } from '../../../components/ui/input'
import { Textarea } from '../../../components/ui/textarea'
import { INTERACTION_TYPES } from '../constants/interactionTypes'
import { SUMMARY_MAX_LENGTH, type InteractionFormErrors, type InteractionFormValues } from '../services/interactionForm'
import type { FieldChange } from '../types/formChange'
import { ChoiceGroup } from './ChoiceGroup'

const FORM_ID = 'nouvel-echange'

interface InteractionDialogProps {
  contactName: string
  values: InteractionFormValues
  errors: InteractionFormErrors
  today: string
  recording: boolean
  onChange: FieldChange<InteractionFormValues>
  onSubmit: () => void
  onClose: () => void
}

export const InteractionDialog = (props: InteractionDialogProps) => {
  const { contactName, values, errors, today, recording, onChange, onSubmit, onClose } = props

  return (
    <Dialog
      open
      onClose={onClose}
      title="Enregistrer un échange"
      description={`Avec ${contactName}. Un échange enregistré ne se modifie plus.`}
      footer={
        <>
          <Button variant="secondary" onClick={onClose} disabled={recording}>
            Annuler
          </Button>
          <Button type="submit" form={FORM_ID} loading={recording}>
            Enregistrer l’échange
          </Button>
        </>
      }
    >
      <form
        id={FORM_ID}
        noValidate
        className="flex flex-col gap-5"
        onSubmit={(event) => {
          event.preventDefault()
          onSubmit()
        }}
      >
        <ChoiceGroup label="Type d’échange" options={INTERACTION_TYPES} value={values.type} onChange={(type) => onChange('type', type)} />
        <Textarea
          label="Résumé"
          required
          rows={4}
          maxLength={SUMMARY_MAX_LENGTH}
          placeholder="Ex. Commande de 20 sacs de riz confirmée pour vendredi"
          value={values.summary}
          invalid={errors.summary !== undefined}
          helper={errors.summary}
          onChange={(event) => onChange('summary', event.target.value)}
        />
        <Input
          label="Date de l’échange"
          type="date"
          required
          max={today}
          value={values.date}
          invalid={errors.date !== undefined}
          helper={errors.date}
          containerClassName="sm:max-w-[240px]"
          onChange={(event) => onChange('date', event.target.value)}
        />
      </form>
    </Dialog>
  )
}
