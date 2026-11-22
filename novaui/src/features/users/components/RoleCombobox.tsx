import { Combobox } from '../../../components/ui/combobox'
import type { Role } from '../../../types/auth'
import { ROLE_OPTIONS, roleOption } from '../constants/roles'

interface RoleComboboxProps {
  value: Role | null
  error?: string
  onChange: (role: Role | null) => void
}

export const RoleCombobox = ({ value, error, onChange }: RoleComboboxProps) => (
  <Combobox
    label="Rôle"
    placeholder="Choisir un rôle"
    options={ROLE_OPTIONS}
    value={roleOption(value ?? undefined)}
    invalid={Boolean(error)}
    helper={error}
    onChange={(option) => onChange(option ? (option.value as Role) : null)}
  />
)
