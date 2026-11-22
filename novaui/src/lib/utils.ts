import { clsx } from 'clsx';
import type { ClassValue } from 'clsx';
import { extendTailwindMerge } from 'tailwind-merge';

// Déclare les tailles de texte du DS (styles.css) pour que tailwind-merge
// ne les confonde pas avec des couleurs (`text-body-m` vs `text-text-primary`).
const twMerge = extendTailwindMerge({
  extend: {
    classGroups: {
      'font-size': [
        {
          text: [
            'mono-xs',
            'caption',
            'button-s',
            'body-s',
            'body-m',
            'body-l',
            'h3',
            'h2',
            'h1',
            'display',
          ],
        },
      ],
      shadow: [{ shadow: ['card', 'hover', 'overlay', 'focus-ring'] }],
    },
  },
});

export function cn(...inputs: ClassValue[]): string {
  return twMerge(clsx(inputs));
}
