import { render, screen } from '@testing-library/react'
import { HomeScreen } from '../HomeScreen'
import { APP_NAME, APP_TAGLINE } from '../../../constants/appInfo'

describe('HomeScreen', () => {
  it('shouldDisplayAppNameAndTagline', () => {
    render(<HomeScreen />)

    expect(screen.getByRole('heading', { name: APP_NAME })).toBeInTheDocument()
    expect(screen.getByText(APP_TAGLINE)).toBeInTheDocument()
  })
})
