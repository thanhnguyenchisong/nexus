import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { aiApi } from '../api/ai';

/** Fetch existing AI summary (no generation) */
export const useAiSummary = (articleId: number | null) => {
  return useQuery({
    queryKey: ['ai-summary', articleId],
    queryFn: () => aiApi.getSummary(articleId!),
    enabled: articleId != null,
    retry: false,           // 404 means not generated yet — don't retry
    staleTime: 60 * 60 * 1000, // 1 hour — summaries don't change
  });
};

/** Generate AI summary on-demand */
export const useGenerateSummary = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (articleId: number) => aiApi.generateSummary(articleId),
    onSuccess: (data) => {
      queryClient.setQueryData(['ai-summary', data.articleId], data);
    },
  });
};
