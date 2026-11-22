export const fieldControlClass = [
  'flex w-full items-center gap-2 rounded-sm border border-line-strong bg-surface-solid px-3.5 transition-shadow',
  'focus-within:border-focus-ring focus-within:shadow-focus-ring',
  'has-[:disabled]:opacity-40 has-[[aria-invalid=true]]:border-status-critical-fg',
].join(' ');

export const helperIdFor = (id: string): string => `${id}-helper`;
