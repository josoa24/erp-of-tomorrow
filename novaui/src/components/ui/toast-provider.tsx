import { useCallback, useMemo, useState } from 'react';
import type { ReactNode } from 'react';
import { Toast } from './toast';
import { ToastContext } from './toast-context';
import type { ToastOptions } from './toast-context';

type ActiveToast = ToastOptions & { id: number };

export function ToastProvider({ children }: { children: ReactNode }) {
  const [toast, setToast] = useState<ActiveToast | null>(null);

  const showToast = useCallback((options: ToastOptions) => {
    setToast({ ...options, id: Date.now() });
  }, []);
  const dismiss = useCallback(() => setToast(null), []);
  const value = useMemo(() => ({ showToast }), [showToast]);

  return (
    <ToastContext.Provider value={value}>
      {children}
      <div className="pointer-events-none fixed inset-x-0 bottom-28 z-50 flex justify-center px-4 [&>*]:pointer-events-auto">
        {toast && (
          <Toast
            key={toast.id}
            message={toast.message}
            actionLabel={toast.actionLabel}
            onAction={toast.onAction}
            onDismiss={dismiss}
          />
        )}
      </div>
    </ToastContext.Provider>
  );
}
