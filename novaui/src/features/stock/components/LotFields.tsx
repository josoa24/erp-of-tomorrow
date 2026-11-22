import { NewTag } from '../../../components/module/NewTag'
import { Input } from '../../../components/ui/input'
import { cn } from '@/lib/utils'
import { LOT_NUMBER_MAX_LENGTH, type MovementFormErrors, type MovementFormValues } from '../services/movementForm'

interface LotFieldsProps {
  values: Pick<MovementFormValues, 'lotNumber' | 'expiryDate'>
  errors: MovementFormErrors
  today: string
  highlighted: boolean
  onChange: (key: 'lotNumber' | 'expiryDate', value: string) => void
}

export const LotFields = ({ values, errors, today, highlighted, onChange }: LotFieldsProps) => (
  <fieldset className={cn('flex flex-col gap-4', highlighted && 'rounded-lg p-3 highlight-added')}>
    <legend className="flex items-center gap-2 text-label text-ink-primary max-sm:mb-2">
      Lot et péremption
      {highlighted && <NewTag />}
    </legend>
    <p className="text-micro text-ink-tertiary">
      Facultatif. Une alerte apparaît 30 jours avant la péremption.
    </p>
    <div className="grid gap-4 sm:grid-cols-2">
      <Input
        label="N° de lot"
        value={values.lotNumber}
        maxLength={LOT_NUMBER_MAX_LENGTH}
        placeholder="Ex. LOT-2611-A"
        autoComplete="off"
        invalid={Boolean(errors.lotNumber)}
        helper={errors.lotNumber}
        onChange={(event) => onChange('lotNumber', event.target.value)}
      />
      <Input
        label="Date de péremption"
        type="date"
        min={today}
        value={values.expiryDate}
        invalid={Boolean(errors.expiryDate)}
        helper={errors.expiryDate}
        onChange={(event) => onChange('expiryDate', event.target.value)}
      />
    </div>
  </fieldset>
)
