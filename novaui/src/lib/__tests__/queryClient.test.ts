import { queryClient } from '../queryClient'

describe('queryClient', () => {
  it('shouldUseDefaultQueryOptions', () => {
    const options = queryClient.getDefaultOptions().queries
    expect(options?.staleTime).toBe(30_000)
    expect(options?.retry).toBe(1)
  })
})
