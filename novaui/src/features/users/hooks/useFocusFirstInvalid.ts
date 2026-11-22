import { useEffect } from 'react'

export const useFocusFirstInvalid = (formId: string, attempt: number) => {
  useEffect(() => {
    if (attempt === 0) return
    document.getElementById(formId)?.querySelector<HTMLElement>('[aria-invalid="true"]')?.focus()
  }, [formId, attempt])
}
