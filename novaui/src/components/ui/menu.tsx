import type { ReactNode } from 'react';
import { createPortal } from 'react-dom';
import { cn } from '@/lib/utils';
import { MenuContext } from './menu-context';
import type { MenuAlign } from './menu-position';
import { useMenu } from './use-menu';
import type { MenuTriggerProps } from './use-menu';

export type MenuProps = {
  label: string;
  trigger: (props: MenuTriggerProps) => ReactNode;
  align?: MenuAlign;
  className?: string;
  children: ReactNode;
};

export function Menu({ label, trigger, align = 'end', className, children }: MenuProps) {
  const { open, close, triggerProps, contentRef, menuId, onContentKeyDown, position } = useMenu(align);

  const content = (
    <div
      ref={contentRef}
      id={menuId}
      role="menu"
      aria-label={label}
      onKeyDown={onContentKeyDown}
      style={position}
      className={cn(
        'fixed z-50 flex w-max min-w-[220px] max-w-[calc(100vw-32px)] flex-col gap-0.5',
        'rounded-md bg-surface-solid p-1.5 shadow-elevation-2',
        className,
      )}
    >
      {children}
    </div>
  );

  return (
    <MenuContext.Provider value={{ close }}>
      <div className="relative inline-flex">
        {trigger(triggerProps)}
        {open && createPortal(content, document.body)}
      </div>
    </MenuContext.Provider>
  );
}
