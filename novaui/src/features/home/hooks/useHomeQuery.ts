import { useQuery } from '@tanstack/react-query'

export const useHomeQuery = <T>(key: string, load: (signal: AbortSignal) => Promise<T>, enabled: boolean) => {
  const query = useQuery({ queryKey: ['home', key], queryFn: ({ signal }) => load(signal), enabled, retry: false })
  return { data: query.data, loading: enabled && query.isPending, failed: query.isError }
}
