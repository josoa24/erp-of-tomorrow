import type { ReactNode } from 'react'

export const SectionTitle = ({ id, children }: { id: string; children: ReactNode }) => (
  <h2 id={id} className="text-caption text-ink-secondary uppercase">
    {children}
  </h2>
)
