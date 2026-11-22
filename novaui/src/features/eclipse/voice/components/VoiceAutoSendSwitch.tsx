interface VoiceAutoSendSwitchProps {
  enabled: boolean
  onChange: (enabled: boolean) => void
}

export const VoiceAutoSendSwitch = ({ enabled, onChange }: VoiceAutoSendSwitchProps) => (
  <button
    type="button"
    role="switch"
    aria-checked={enabled}
    onClick={() => onChange(!enabled)}
    className="inline-flex items-center gap-2 rounded-pill text-micro text-ink-secondary outline-none focus-visible:shadow-focus-ring"
  >
    <span className={enabled ? 'flex h-4 w-7 items-center rounded-pill bg-brand-violet p-0.5' : 'flex h-4 w-7 items-center rounded-pill bg-line-strong p-0.5'}>
      <span className={enabled ? 'size-3 translate-x-3 rounded-pill bg-surface-solid transition-transform' : 'size-3 rounded-pill bg-surface-solid transition-transform'} />
    </span>
    Envoi automatique
  </button>
)
