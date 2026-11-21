import { RouterProvider } from 'react-router-dom'
import { appRouter } from './navigation/appRouter'

const App = () => <RouterProvider router={appRouter} />

export default App
