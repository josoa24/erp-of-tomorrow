import { clsx } from 'clsx';
import type { ClassValue } from 'clsx';
import { extendTailwindMerge } from 'tailwind-merge';

const NOVA_TEXT_STYLES = [
  'display-xl',
  'display-l',
  'display-m',
  'number-m',
  'title',
  'heading',
  'body',
  'body-strong',
  'body-s',
  'label',
  'caption',
  'micro',
];

const NOVA_SHADOWS = ['elevation-1', 'elevation-2', 'focus-ring', 'eclipse-hover'];

const twMerge = extendTailwindMerge({
  extend: {
    classGroups: {
      'font-size': [{ text: NOVA_TEXT_STYLES }],
      shadow: [{ shadow: NOVA_SHADOWS }],
    },
  },
});

export function cn(...inputs: ClassValue[]): string {
  return twMerge(clsx(inputs));
}
