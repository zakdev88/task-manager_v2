import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import TaskManagementDashboard from './components/Dashboard'

const client = new QueryClient();
function App() {
  return (
    <QueryClientProvider client={client}>
      <TaskManagementDashboard />
    </QueryClientProvider>
  )
}

export default App
