import type { ReactNode } from 'react';

export type ComboboxOption = {
  value: string;
  label: string;
  description?: string;
};

export type ComboboxItem = { key: string; option: ComboboxOption | null };

export type ComboboxProps = {
  label: string;
  options: ComboboxOption[];
  value?: ComboboxOption | null;
  onChange?: (option: ComboboxOption | null) => void;
  onSelect?: (option: ComboboxOption) => void;
  onQueryChange?: (query: string) => void;
  loading?: boolean;
  placeholder?: string;
  emptyText?: string;
  emptyOptionLabel?: string;
  clearable?: boolean;
  invalid?: boolean;
  helper?: ReactNode;
  disabled?: boolean;
  unavailableText?: string;
  hideLabel?: boolean;
  renderOption?: (option: ComboboxOption) => ReactNode;
  id?: string;
  className?: string;
};
