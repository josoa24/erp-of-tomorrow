import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { Tabs } from '../tabs'

const items = [
  { value: 'a', label: 'Alpha' },
  { value: 'b', label: 'Bravo', disabled: true },
  { value: 'c', label: 'Charlie' },
]

describe('Tabs', () => {
  it('shouldMarkSelectedTab', () => {
    render(<Tabs items={items} value="a" onValueChange={() => {}} aria-label="Vues" />)
    expect(screen.getByRole('tab', { name: 'Alpha' })).toHaveAttribute('aria-selected', 'true')
  })

  it('shouldCallOnValueChange_whenClicked', async () => {
    const onValueChange = vi.fn()
    render(<Tabs items={items} value="a" onValueChange={onValueChange} aria-label="Vues" />)
    await userEvent.click(screen.getByRole('tab', { name: 'Charlie' }))
    expect(onValueChange).toHaveBeenCalledWith('c')
  })

  it('shouldSkipDisabledTab_whenArrowRight', async () => {
    const onValueChange = vi.fn()
    render(<Tabs items={items} value="a" onValueChange={onValueChange} aria-label="Vues" />)
    screen.getByRole('tab', { name: 'Alpha' }).focus()
    await userEvent.keyboard('{ArrowRight}')
    expect(onValueChange).toHaveBeenCalledWith('c')
  })
})
