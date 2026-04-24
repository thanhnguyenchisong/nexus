import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { bookmarksApi, collectionsApi } from '../api/knowledge';
import type { BookmarkFormData, BookmarkUpdateData } from '../api/types';

// ─── Bookmark hooks ────────────────────────────────────────────────────────────

export const useBookmarks = (status?: string) =>
  useQuery({
    queryKey: ['bookmarks', status],
    queryFn: () => bookmarksApi.getAll(status),
    staleTime: 60_000,
  });

export const useBookmarkByArticle = (articleId: number | null) =>
  useQuery({
    queryKey: ['bookmark-by-article', articleId],
    queryFn: () => bookmarksApi.getByArticleId(articleId!),
    enabled: articleId != null,
    retry: false,  // 404 = not bookmarked
    staleTime: 60_000,
  });

export const useCreateBookmark = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (data: BookmarkFormData) => bookmarksApi.create(data),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['bookmarks'] });
      qc.invalidateQueries({ queryKey: ['bookmark-by-article'] });
    },
  });
};

export const useUpdateBookmark = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: BookmarkUpdateData }) =>
      bookmarksApi.update(id, data),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['bookmarks'] }),
  });
};

export const useDeleteBookmark = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: number) => bookmarksApi.delete(id),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['bookmarks'] });
      qc.invalidateQueries({ queryKey: ['bookmark-by-article'] });
    },
  });
};

export const useDeleteBookmarkByArticle = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (articleId: number) => bookmarksApi.deleteByArticleId(articleId),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['bookmarks'] });
      qc.invalidateQueries({ queryKey: ['bookmark-by-article'] });
    },
  });
};

// ─── Collection hooks ──────────────────────────────────────────────────────────

export const useCollections = () =>
  useQuery({
    queryKey: ['collections'],
    queryFn: () => collectionsApi.getAll(),
    staleTime: 60_000,
  });

export const useCreateCollection = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ name, description }: { name: string; description?: string }) =>
      collectionsApi.create(name, description),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['collections'] }),
  });
};

export const useDeleteCollection = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: number) => collectionsApi.delete(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['collections'] }),
  });
};
