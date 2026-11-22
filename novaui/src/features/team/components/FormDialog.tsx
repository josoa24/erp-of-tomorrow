import type { ReactNode } from 'react'
import { Button } from '../../../components/ui/button'
import { Dialog } from '../../../components/ui/dialog'

interface FormDialogProps {
  formId: string
  title: string
  description: string
  submitLabel: string
  saving: boolean
  submitDisabled?: boolean
  onSubmit: () => void
  onClose: () => void
  children: ReactNode
}

export const FormDialog = ({ formId, title, description, submitLabel, saving, submitDisabled, onSubmit, onClose, children }: FormDialogProps) => (
  <Dialog
    open
    onClose={onClose}
    title={title}
    description={description}
    footer={
      <>
        <Button variant="secondary" onClick={onClose} disabled={saving}>
          Annuler
        </Button>
        <Button type="submit" form={formId} loading={saving} disabled={submitDisabled}>
          {submitLabel}
        </Button>
      </>
    }
  >
    <form
      id={formId}
      noValidate
      className="flex flex-col gap-5"
      onSubmit={(event) => {
        event.preventDefault()
        onSubmit()
      }}
    >
      {children}
    </form>
  </Dialog>
)
