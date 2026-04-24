import { apiClient } from './client';
import type { AiSummary } from './types';

export const aiApi = {
  /** Get existing summary (no generation) */
  getSummary: async (articleId: number): Promise<AiSummary> => {
    const res = await apiClient.get<AiSummary>(`/api/v1/articles/${articleId}/summary`);
    return res.data;
  },

  /** Generate (or return existing) summary on demand */
  generateSummary: async (articleId: number): Promise<AiSummary> => {
    const res = await apiClient.post<AiSummary>(`/api/v1/articles/${articleId}/summary`);
    return res.data;
  },

  /** Trigger batch processing */
  triggerBatch: async (): Promise<void> => {
    await apiClient.post('/api/v1/ai/batch');
  },
};
