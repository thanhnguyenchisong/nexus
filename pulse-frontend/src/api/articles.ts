import { apiClient } from './client';
import type { Article, ArticleFilters, PagedResponse } from './types';

export const articlesApi = {
  getArticles: async (filters: ArticleFilters = {}): Promise<PagedResponse<Article>> => {
    const params: Record<string, string | number> = {};
    if (filters.domain)   params.domain   = filters.domain;
    if (filters.language) params.language = filters.language;
    if (filters.category) params.category = filters.category;
    if (filters.q)        params.q        = filters.q;
    params.page = filters.page ?? 0;
    params.size = filters.size ?? 20;

    const res = await apiClient.get<PagedResponse<Article>>('/api/v1/articles', { params });
    return res.data;
  },

  getArticle: async (id: number): Promise<Article> => {
    const res = await apiClient.get<Article>(`/api/v1/articles/${id}`);
    return res.data;
  },

  triggerCollection: async (): Promise<void> => {
    await apiClient.post('/api/v1/collector/trigger');
  },
};
