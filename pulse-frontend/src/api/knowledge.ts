import { apiClient } from './client';
import type { Bookmark, BookmarkFormData, BookmarkUpdateData, Collection } from './types';

// ─── Bookmarks ────────────────────────────────────────────────────────────────

export const bookmarksApi = {
  getAll: async (status?: string): Promise<Bookmark[]> => {
    const params: Record<string, string> = {};
    if (status) params.status = status;
    const res = await apiClient.get<Bookmark[]>('/api/v1/bookmarks', { params });
    return res.data;
  },

  getById: async (id: number): Promise<Bookmark> => {
    const res = await apiClient.get<Bookmark>(`/api/v1/bookmarks/${id}`);
    return res.data;
  },

  getByArticleId: async (articleId: number): Promise<Bookmark> => {
    const res = await apiClient.get<Bookmark>(`/api/v1/bookmarks/by-article/${articleId}`);
    return res.data;
  },

  create: async (data: BookmarkFormData): Promise<Bookmark> => {
    const res = await apiClient.post<Bookmark>('/api/v1/bookmarks', data);
    return res.data;
  },

  update: async (id: number, data: BookmarkUpdateData): Promise<Bookmark> => {
    const res = await apiClient.patch<Bookmark>(`/api/v1/bookmarks/${id}`, data);
    return res.data;
  },

  delete: async (id: number): Promise<void> => {
    await apiClient.delete(`/api/v1/bookmarks/${id}`);
  },

  deleteByArticleId: async (articleId: number): Promise<void> => {
    await apiClient.delete(`/api/v1/bookmarks/by-article/${articleId}`);
  },
};

// ─── Collections ──────────────────────────────────────────────────────────────

export const collectionsApi = {
  getAll: async (): Promise<Collection[]> => {
    const res = await apiClient.get<Collection[]>('/api/v1/collections');
    return res.data;
  },

  getById: async (id: number): Promise<Collection> => {
    const res = await apiClient.get<Collection>(`/api/v1/collections/${id}`);
    return res.data;
  },

  getArticles: async (collectionId: number): Promise<Bookmark[]> => {
    const res = await apiClient.get<Bookmark[]>(`/api/v1/collections/${collectionId}/articles`);
    return res.data;
  },

  create: async (name: string, description?: string): Promise<Collection> => {
    const res = await apiClient.post<Collection>('/api/v1/collections', { name, description });
    return res.data;
  },

  update: async (id: number, name?: string, description?: string): Promise<Collection> => {
    const res = await apiClient.patch<Collection>(`/api/v1/collections/${id}`, { name, description });
    return res.data;
  },

  delete: async (id: number): Promise<void> => {
    await apiClient.delete(`/api/v1/collections/${id}`);
  },

  addArticle: async (collectionId: number, savedArticleId: number): Promise<void> => {
    await apiClient.post(`/api/v1/collections/${collectionId}/articles/${savedArticleId}`);
  },

  removeArticle: async (collectionId: number, savedArticleId: number): Promise<void> => {
    await apiClient.delete(`/api/v1/collections/${collectionId}/articles/${savedArticleId}`);
  },
};
