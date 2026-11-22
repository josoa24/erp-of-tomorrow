import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { RouterProvider, createMemoryHistory, createRootRoute, createRoute, createRouter } from '@tanstack/react-router'
import { render } from '@testing-library/react'
import type { FunctionComponent } from 'react'
import { validateShellSearch } from '../navigation/shellSearch'

const PAGES = ['/', '/ventes', '/stock', '/equipe', '/relations', '/profil', '/connexion', '/erreur']

export const renderWithRouter = async (Component: FunctionComponent, initialPath = '/stock') => {
  const rootRoute = createRootRoute({ component: Component, validateSearch: validateShellSearch })
  const pages = PAGES.map((path) => createRoute({ getParentRoute: () => rootRoute, path, component: () => null }))
  const modulePage = createRoute({ getParentRoute: () => rootRoute, path: '/modules/$slug', component: () => null })
  const router = createRouter({
    routeTree: rootRoute.addChildren([...pages, modulePage]),
    history: createMemoryHistory({ initialEntries: [initialPath] }),
  })
  const queryClient = new QueryClient({ defaultOptions: { queries: { retry: false } } })
  await router.load()
  const view = render(
    <QueryClientProvider client={queryClient}>
      <RouterProvider router={router} />
    </QueryClientProvider>,
  )
  return { router, queryClient, ...view }
}
