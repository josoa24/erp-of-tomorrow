import { Ellipsis } from 'lucide-react'
import { IconButton } from '../ui/icon-button'
import { Menu } from '../ui/menu'
import { MenuItem } from '../ui/menu-item'
import type { RowAction } from './types'

interface RowActionsMenuProps {
  rowLabel: string
  actions: RowAction[]
}

export const RowActionsMenu = ({ rowLabel, actions }: RowActionsMenuProps) => {
  if (actions.length === 0) return null
  const label = `Actions pour ${rowLabel}`

  return (
    <Menu
      label={label}
      trigger={(props) => (
        <IconButton aria-label={label} variant="ghost" size="l" {...props}>
          <Ellipsis />
        </IconButton>
      )}
    >
      {actions.map((action) => (
        <MenuItem key={action.key} icon={action.icon} tone={action.tone} onSelect={action.onSelect}>
          {action.label}
        </MenuItem>
      ))}
    </Menu>
  )
}
