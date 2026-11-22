import type { LucideIcon } from 'lucide-react';
import type { ComponentProps } from 'react';
import { cn } from '@/lib/utils';
import { useMenuContext } from './menu-context';

export type MenuItemProps = Omit<ComponentProps<'button'>, 'onSelect' | 'type' | 'role'> & {
  icon?: LucideIcon;
  tone?: 'default' | 'danger';
  onSelect?: () => void;
};

export function MenuItem({ icon: Icon, tone = 'default', onSelect, className, children, ...props }: MenuItemProps) {
  const { close } = useMenuContext();

  return (
    <button
      type="button"
      role="menuitem"
      tabIndex={-1}
      onClick={() => {
        close();
        onSelect?.();
      }}
      className={cn(
        'flex h-10 w-full shrink-0 items-center gap-3 rounded-sm px-3 text-left text-body-s outline-none',
        'transition-colors enabled:hover:bg-state-hover focus-visible:bg-state-hover focus-visible:shadow-[inset_0_0_0_2px_var(--color-focus-ring)] disabled:opacity-40',
        tone === 'danger' ? 'text-action-danger' : 'text-ink-primary',
        className,
      )}
      {...props}
    >
      {Icon && <Icon aria-hidden="true" className="size-[18px] shrink-0" />}
      {children}
    </button>
  );
}

export function MenuSeparator() {
  return <div role="separator" className="my-1 h-px shrink-0 bg-line-subtle" />;
}
