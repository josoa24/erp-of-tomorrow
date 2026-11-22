import { Input } from '../../../components/ui/input'
import type { ContactFormErrors, ContactFormValues } from '../services/contactForm'
import type { FieldChange } from '../types/formChange'

interface ContactIdentityFieldsProps {
  values: ContactFormValues
  errors: ContactFormErrors
  onChange: FieldChange<ContactFormValues>
}

export const ContactIdentityFields = ({ values, errors, onChange }: ContactIdentityFieldsProps) => (
  <>
    <Input
      label="Nom"
      required
      maxLength={255}
      autoComplete="organization"
      placeholder={values.kind === 'SUPPLIER' ? 'Tiko Distribution' : 'Épicerie Rova'}
      value={values.name}
      invalid={errors.name !== undefined}
      helper={errors.name}
      onChange={(event) => onChange('name', event.target.value)}
    />
    <div className="grid gap-4 sm:grid-cols-2">
      <Input
        label="Téléphone"
        type="tel"
        maxLength={40}
        autoComplete="tel"
        placeholder="034 12 345 67"
        value={values.phone}
        onChange={(event) => onChange('phone', event.target.value)}
      />
      <Input
        label="E-mail"
        type="email"
        maxLength={255}
        autoComplete="email"
        placeholder="contact@boutique.mg"
        value={values.email}
        invalid={errors.email !== undefined}
        helper={errors.email}
        onChange={(event) => onChange('email', event.target.value)}
      />
    </div>
    <Input
      label="Ville"
      maxLength={80}
      autoComplete="address-level2"
      placeholder="Antananarivo"
      value={values.city}
      onChange={(event) => onChange('city', event.target.value)}
    />
  </>
)
