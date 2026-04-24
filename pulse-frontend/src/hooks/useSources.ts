import { useQuery } from '@tanstack/react-query';
import { sourcesApi } from '../api/sources';

export const useSources = (domain?: string) => {
  return useQuery({
    queryKey: ['sources', domain],
    queryFn: () => sourcesApi.getSources(domain),
    staleTime: 10 * 60 * 1000, // 10 minutes — sources don't change often
  });
};
