import { NewTag } from '../../../components/module/NewTag'
import { Input } from '../../../components/ui/input'
import { cn } from '../../../lib/utils'
import { showsFiscalIds, type ContactFormErrors, type ContactFormValues } from '../services/contactForm'
import type { FieldChange } from '../types/formChange'

interface ContactAdaptationFieldsProps {
  values: ContactFormValues
  errors: ContactFormErrors
  highlighted: boolean
  onChange: FieldChange<ContactFormValues>
}

export const ContactAdaptationFields = ({ values, errors, highlighted, onChange }: ContactAdaptationFieldsProps) => {
  const fiscal = showsFiscalIds(values)
  const customer = values.kind === 'CUSTOMER'

  return (
    <div
      role="group"
      aria-label="Fiscalité et crédit"
      className={cn('flex flex-col gap-4', highlighted && 'highlight-added rounded-lg p-4')}
    >
      {highlighted && (
        <p className="flex items-center gap-2 text-label text-status-success-fg">
          Ajouté par Eclipse <NewTag />
        </p>
      )}
      {fiscal && (
        <div className="grid gap-4 sm:grid-cols-2">
          <Input label="NIF" inputMode="numeric" maxLength={20} placeholder="4000 123 456" value={values.nif} onChange={(event) => onChange('nif', event.target.value)} />
          <Input label="STAT" inputMode="numeric" maxLength={30} placeholder="47111 11 2019 0 00123" value={values.stat} onChange={(event) => onChange('stat', event.target.value)} />
        </div>
      )}
      {customer && (
        <Input
          label="Plafond de crédit (Ar)"
          inputMode="numeric"
          maxLength={16}
          placeholder="2 000 000"
          value={values.creditLimit}
          invalid={errors.creditLimit !== undefined}
          helper={errors.creditLimit ?? 'Laissez vide si ce client n’achète pas à crédit.'}
          onChange={(event) => onChange('creditLimit', event.target.value)}
        />
      )}
    </div>
  )
}
