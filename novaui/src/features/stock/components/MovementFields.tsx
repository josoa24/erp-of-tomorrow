import { Input } from '../../../components/ui/input'
import { REASON_MAX_LENGTH, type MovementFormErrors, type MovementFormValues } from '../services/movementForm'

interface MovementFieldsProps {
  quantityLabel: string
  quantityHint?: string
  reasonPlaceholder: string
  values: Pick<MovementFormValues, 'quantity' | 'reason'>
  errors: MovementFormErrors
  onChange: (key: 'quantity' | 'reason', value: string) => void
}

export const MovementFields = ({
  quantityLabel,
  quantityHint,
  reasonPlaceholder,
  values,
  errors,
  onChange,
}: MovementFieldsProps) => (
  <>
    <Input
      label={quantityLabel}
      type="number"
      inputMode="numeric"
      min={1}
      step={1}
      required
      value={values.quantity}
      invalid={Boolean(errors.quantity)}
      helper={errors.quantity ?? quantityHint}
      onChange={(event) => onChange('quantity', event.target.value)}
    />
    <Input
      label="Motif (facultatif)"
      value={values.reason}
      maxLength={REASON_MAX_LENGTH}
      placeholder={reasonPlaceholder}
      onChange={(event) => onChange('reason', event.target.value)}
    />
  </>
)
