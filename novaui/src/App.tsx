import { RouterProvider } from '@tanstack/react-router'
import { appRouter } from './navigation/appRouter'

const App = () => <RouterProvider router={appRouter} />

export default App
