const BARS = [0, 1, 2, 3, 4]

export const VoiceLevelMeter = ({ level }: { level: number }) => (
  <span aria-hidden="true" className="flex h-5 items-end gap-0.5">
    {BARS.map((bar) => (
      <span
        key={bar}
        className={level >= (bar + 1) / 6 ? 'w-1 rounded-pill bg-status-critical-fg' : 'w-1 rounded-pill bg-line-strong'}
        style={{ height: `${40 + bar * 15}%` }}
      />
    ))}
  </span>
)
