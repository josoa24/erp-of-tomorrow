import { cva } from 'class-variance-authority';

export const statusPillVariants = cva(
  'inline-flex h-[30px] shrink-0 items-center gap-1.5 rounded-pill pr-3 pl-2.5 text-label whitespace-nowrap [&_svg]:size-4 [&_svg]:shrink-0',
  {
    variants: {
      tone: { success: '', warning: '', critical: '', info: '', neutral: 'text-ink-secondary' },
      emphasis: { soft: '', vivid: 'text-ink-primary' },
    },
    compoundVariants: [
      { tone: 'success', emphasis: 'soft', className: 'bg-status-success-bg text-status-success-fg' },
      { tone: 'warning', emphasis: 'soft', className: 'bg-status-warning-bg text-status-warning-fg' },
      { tone: 'critical', emphasis: 'soft', className: 'bg-status-critical-bg text-status-critical-fg' },
      { tone: 'info', emphasis: 'soft', className: 'bg-status-info-bg text-status-info-fg' },
      { tone: 'neutral', emphasis: 'soft', className: 'bg-surface-sunken' },
      { tone: 'success', emphasis: 'vivid', className: 'bg-status-success-vivid' },
      { tone: 'warning', emphasis: 'vivid', className: 'bg-status-warning-vivid' },
      { tone: 'critical', emphasis: 'vivid', className: 'bg-status-critical-vivid' },
      { tone: 'info', emphasis: 'vivid', className: 'bg-status-info-vivid' },
      { tone: 'neutral', emphasis: 'vivid', className: 'bg-line-subtle text-ink-secondary' },
    ],
    defaultVariants: { tone: 'neutral', emphasis: 'soft' },
  },
);
