import { apiClient } from './client';
import type { Source } from './types';

export interface SourceUpdateBody {
  status?: string;
  fetchIntervalMinutes?: number;
}

export const sourcesApi = {
  getSources: async (domain?: string): Promise<Source[]> => {
    const params: Record<string, string> = {};
    if (domain) params.domain = domain;
    const res = await apiClient.get<Source[]>('/api/v1/sources', { params });
    return res.data;
  },

  getSource: async (id: number): Promise<Source> => {
    const res = await apiClient.get<Source>(`/api/v1/sources/${id}`);
    return res.data;
  },

  updateSource: async (id: number, body: SourceUpdateBody): Promise<Source> => {
    const res = await apiClient.patch<Source>(`/api/v1/sources/${id}`, body);
    return res.data;
  },

  triggerFetch: async (id: number): Promise<void> => {
    await apiClient.post(`/api/v1/sources/${id}/fetch`);
  },
};
