import { useQuery, keepPreviousData } from '@tanstack/react-query';
import { articlesApi } from '../api/articles';
import type { ArticleFilters } from '../api/types';

export const useArticles = (filters: ArticleFilters = {}) => {
  return useQuery({
    queryKey: ['articles', filters],
    queryFn: () => articlesApi.getArticles(filters),
    placeholderData: keepPreviousData,
    staleTime: 2 * 60 * 1000, // 2 minutes
  });
};
