import { createContext, useContext } from 'react';

export type ToastOptions = {
  message: string;
  actionLabel?: string;
  onAction?: () => void;
};

export type ToastContextValue = { showToast: (options: ToastOptions) => void };

export const ToastContext = createContext<ToastContextValue>({ showToast: () => undefined });

export const useToast = (): ToastContextValue => useContext(ToastContext);
