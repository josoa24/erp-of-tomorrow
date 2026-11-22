import { Button } from '../../../components/ui/button'
import { Dialog } from '../../../components/ui/dialog'
import { CONTACT_KIND_OPTIONS, CUSTOMER_CATEGORY_OPTIONS } from '../constants/contactOptions'
import type { ContactFormErrors, ContactFormValues } from '../services/contactForm'
import type { AdaptationView } from '../types/adaptationView'
import type { FieldChange } from '../types/formChange'
import { ChoiceGroup } from './ChoiceGroup'
import { ContactAdaptationFields } from './ContactAdaptationFields'
import { ContactIdentityFields } from './ContactIdentityFields'

const FORM_ID = 'nouveau-contact'

interface NewContactDialogProps {
  values: ContactFormValues
  errors: ContactFormErrors
  adaptation: AdaptationView
  creating: boolean
  onChange: FieldChange<ContactFormValues>
  onSubmit: () => void
  onClose: () => void
}

export const NewContactDialog = ({ values, errors, adaptation, creating, onChange, onSubmit, onClose }: NewContactDialogProps) => (
  <Dialog
    open
    onClose={onClose}
    title="Nouveau contact"
    description="Un client est ajouté aux Ventes, un fournisseur au Stock."
    footer={
      <>
        <Button variant="secondary" onClick={onClose} disabled={creating}>
          Annuler
        </Button>
        <Button type="submit" form={FORM_ID} loading={creating}>
          Enregistrer le contact
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
      <ChoiceGroup label="Type de contact" options={CONTACT_KIND_OPTIONS} value={values.kind} onChange={(kind) => onChange('kind', kind)} />
      {values.kind === 'CUSTOMER' && (
        <ChoiceGroup
          label="Catégorie de client"
          options={CUSTOMER_CATEGORY_OPTIONS}
          value={values.category}
          onChange={(category) => onChange('category', category)}
        />
      )}
      <ContactIdentityFields values={values} errors={errors} onChange={onChange} />
      {adaptation.visible && (
        <ContactAdaptationFields values={values} errors={errors} highlighted={adaptation.highlighted} onChange={onChange} />
      )}
    </form>
  </Dialog>
)
