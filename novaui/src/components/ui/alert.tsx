import { CircleAlert, CircleCheck, Info, Sparkle, TriangleAlert, X } from 'lucide-react';
import type { ComponentProps, ReactNode } from 'react';
import { cn } from '@/lib/utils';
import { IconButton } from './icon-button';

export type AlertTone = 'info' | 'success' | 'warning' | 'danger' | 'ai';

const toneIcon: Record<AlertTone, ReactNode> = {
  info: <Info className="text-icon-info" />,
  success: <CircleCheck className="text-icon-success" />,
  warning: <TriangleAlert className="text-icon-warning" />,
  danger: <CircleAlert className="text-icon-danger" />,
  ai: <Sparkle className="fill-current text-icon-ai" />,
};

export type AlertProps = Omit<ComponentProps<'div'>, 'title'> & {
  tone?: AlertTone;
  title: ReactNode;
  description?: ReactNode;
  /** Action à droite, typiquement `<Button variant="ghost" size="s">`. */
  action?: ReactNode;
  /** Affiche le bouton de fermeture. */
  onDismiss?: () => void;
};

/**
 * Message intégré à la page (Figma « Alert »). Surface neutre, icône dans la couleur du ton.
 * Danger/Warning : role="alert" ; Info/Success/AI : role="status".
 */
export function Alert({
  className,
  tone = 'info',
  title,
  description,
  action,
  onDismiss,
  ...props
}: AlertProps) {
  return (
    <div
      role={tone === 'danger' || tone === 'warning' ? 'alert' : 'status'}
      className={cn(
        'flex w-full items-start gap-3 rounded-md border border-border-default bg-bg-surface py-2 pr-2 pl-3',
        className,
      )}
      {...props}
    >
      <span aria-hidden="true" className="flex shrink-0 pt-1">
        <span className="flex h-5 w-4 items-center justify-center [&_svg]:size-4">
          {toneIcon[tone]}
        </span>
      </span>
      <div className="flex min-w-0 flex-1 flex-col gap-0.5 py-1">
        <p className="text-body-m font-medium text-text-primary">{title}</p>
        {description && <p className="text-body-s text-text-secondary">{description}</p>}
      </div>
      {(action || onDismiss) && (
        <div className="flex shrink-0 items-center gap-1">
          {action}
          {onDismiss && (
            <IconButton size="s" aria-label="Fermer" onClick={onDismiss}>
              <X />
            </IconButton>
          )}
        </div>
      )}
    </div>
  );
}
