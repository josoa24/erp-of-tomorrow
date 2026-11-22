export const navigateMock = vi.fn()

export const mockRouter = (pathname = '/') => ({
  useNavigate: () => navigateMock,
  useLocation: ({ select }: { select: (location: { pathname: string }) => unknown }) => select({ pathname }),
})
