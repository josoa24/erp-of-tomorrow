import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { Truck } from 'lucide-react'
import { Checkbox } from '../checkbox'
import { ChoiceChip } from '../choice-chip'
import { Input } from '../input'
import { PasswordField } from '../password-field'
import { Textarea } from '../textarea'

describe('form primitives', () => {
  it('shouldAssociateLabelAndHelper_withInput', () => {
    render(<Input label="Adresse e-mail" helper="ex. hery@boutique-tana.mg" />)

    const input = screen.getByLabelText('Adresse e-mail')
    expect(input).toHaveAccessibleDescription('ex. hery@boutique-tana.mg')
  })

  it('shouldMarkInputInvalid_forPreSubmitHints', () => {
    render(<Input label="Nom" invalid />)

    expect(screen.getByLabelText('Nom')).toHaveAttribute('aria-invalid', 'true')
  })

  it('shouldTogglePasswordVisibility_withPressedState', async () => {
    render(<PasswordField />)
    const toggle = screen.getByRole('button', { name: 'Afficher le mot de passe' })

    expect(screen.getByLabelText('Mot de passe')).toHaveAttribute('type', 'password')
    await userEvent.click(toggle)

    expect(toggle).toHaveAttribute('aria-pressed', 'true')
    expect(screen.getByLabelText('Mot de passe')).toHaveAttribute('type', 'text')
  })

  it('shouldShowCounter_whenTextareaHasMaxLength', () => {
    render(<Textarea label="Description" maxLength={280} value="Livraisons du quartier" onChange={vi.fn()} />)

    expect(screen.getByLabelText('Description')).toBeInTheDocument()
    expect(screen.getByText('22 / 280')).toBeInTheDocument()
  })

  it('shouldToggleCheckbox_throughItsLabel', async () => {
    render(<Checkbox label="Signature du client" />)

    await userEvent.click(screen.getByText('Signature du client'))

    expect(screen.getByRole('checkbox', { name: 'Signature du client' })).toBeChecked()
  })

  it('shouldExposeChoiceChipSelection', () => {
    render(<ChoiceChip label="Livraisons" icon={Truck} selected />)

    const chip = screen.getByRole('button', { name: 'Livraisons' })
    expect(chip).toHaveAttribute('aria-pressed', 'true')
    expect(chip).toHaveClass('bg-surface-night')
  })
})
