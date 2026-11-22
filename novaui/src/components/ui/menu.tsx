import type { ReactNode } from 'react';
import { cn } from '@/lib/utils';
import { MenuContext } from './menu-context';
import { useMenu } from './use-menu';
import type { MenuTriggerProps } from './use-menu';

export type MenuProps = {
  label: string;
  trigger: (props: MenuTriggerProps) => ReactNode;
  align?: 'start' | 'end';
  className?: string;
  children: ReactNode;
};

export function Menu({ label, trigger, align = 'end', className, children }: MenuProps) {
  const { open, close, triggerProps, contentRef, menuId, onContentKeyDown } = useMenu();

  return (
    <MenuContext.Provider value={{ close }}>
      <div className="relative inline-flex">
        {trigger(triggerProps)}
        {open && (
          <div
            ref={contentRef}
            id={menuId}
            role="menu"
            aria-label={label}
            onKeyDown={onContentKeyDown}
            className={cn(
              'absolute top-full z-40 mt-2 flex w-max min-w-[220px] max-w-[calc(100vw-32px)] flex-col gap-0.5',
              'rounded-md bg-surface-solid p-1.5 shadow-elevation-2',
              align === 'end' ? 'right-0' : 'left-0',
              className,
            )}
          >
            {children}
          </div>
        )}
      </div>
    </MenuContext.Provider>
  );
}
