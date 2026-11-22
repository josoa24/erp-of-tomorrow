export const EMPTY_VALUE_LABEL = 'Non renseigné'

export const EmptyValue = () => (
  <span className="text-ink-tertiary">
    <span aria-hidden="true">—</span>
    <span className="sr-only">{EMPTY_VALUE_LABEL}</span>
  </span>
)
