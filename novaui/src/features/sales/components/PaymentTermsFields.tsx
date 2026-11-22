import { ChoiceChip } from '../../../components/ui/choice-chip'
import { CREDIT_DAY_OPTIONS, PAYMENT_METHOD_LABELS, SALE_PAYMENT_METHODS, type CreditDays } from '../services/paymentTerms'
import type { PaymentMethod } from '../types/sale'

interface PaymentTermsFieldsProps {
  method: PaymentMethod
  creditDays: CreditDays
  highlighted: boolean
  onMethod: (method: PaymentMethod) => void
  onCreditDays: (days: CreditDays) => void
}

export const PaymentTermsFields = ({ method, creditDays, highlighted, onMethod, onCreditDays }: PaymentTermsFieldsProps) => (
  <fieldset className={`flex flex-col gap-3 ${highlighted ? 'rounded-md border border-dashed border-status-success-fg p-3' : ''}`}>
    <legend className="mb-1.5 text-label text-ink-primary">Paiement</legend>
    <div className="flex flex-wrap gap-2">
      {SALE_PAYMENT_METHODS.map((option) => (
        <ChoiceChip key={option} label={PAYMENT_METHOD_LABELS[option]} selected={method === option} onClick={() => onMethod(option)} />
      ))}
    </div>
    {method === 'CREDIT' && (
      <div className="flex flex-wrap items-center gap-2" role="group" aria-label="Durée du crédit">
        {CREDIT_DAY_OPTIONS.map((days) => (
          <ChoiceChip key={days} label={`${days} jours`} selected={creditDays === days} onClick={() => onCreditDays(days)} />
        ))}
      </div>
    )}
  </fieldset>
)
