import { fireEvent, render, screen } from '@testing-library/react'
import { SearchField } from '../search-field'

describe('SearchField', () => {
  beforeEach(() => vi.useFakeTimers())
  afterEach(() => vi.useRealTimers())

  it('shouldCallOnSearchOnce_after250ms', () => {
    const onSearch = vi.fn()
    render(<SearchField placeholder="Rechercher un produit…" onSearch={onSearch} />)
    const input = screen.getByRole('searchbox', { name: 'Rechercher un produit…' })

    fireEvent.change(input, { target: { value: 'ri' } })
    fireEvent.change(input, { target: { value: ' riz ' } })
    vi.advanceTimersByTime(249)
    expect(onSearch).not.toHaveBeenCalled()
    vi.advanceTimersByTime(1)

    expect(onSearch).toHaveBeenCalledTimes(1)
    expect(onSearch).toHaveBeenCalledWith('riz')
  })

  it('shouldFocusInput_whenSlashIsPressed', () => {
    render(<SearchField onSearch={vi.fn()} />)

    fireEvent.keyDown(document.body, { key: '/' })

    expect(screen.getByRole('searchbox')).toHaveFocus()
  })

  it('shouldNotStealFocus_whenSlashIsTypedInAnotherField', () => {
    render(
      <>
        <input aria-label="Note" />
        <SearchField onSearch={vi.fn()} />
      </>,
    )
    const note = screen.getByRole('textbox', { name: 'Note' })
    note.focus()

    fireEvent.keyDown(note, { key: '/' })

    expect(note).toHaveFocus()
  })

  it('shouldKeepDefaultValue', () => {
    render(<SearchField defaultValue="sucre" onSearch={vi.fn()} />)

    expect(screen.getByRole('searchbox')).toHaveValue('sucre')
  })
})
