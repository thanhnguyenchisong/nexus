import React, { useCallback } from 'react';
import { FeedFilter } from '../components/feed/FeedFilter';
import { ArticleList } from '../components/article/ArticleList';
import { useArticles } from '../hooks/useArticles';
import { useFilterStore } from '../store/filterStore';
import { useDebounce } from '../hooks/useDebounce';
import { articlesApi } from '../api/articles';
import { RefreshCw } from 'lucide-react';
import { useMutation, useQueryClient } from '@tanstack/react-query';

export const Dashboard = () => {
  const { domain, search, page, setDomain, setPage } = useFilterStore();
  const queryClient = useQueryClient();

  // Debounce search to avoid API call on every keystroke
  const debouncedSearch = useDebounce(search, 400);

  const { data, isLoading, isFetching } = useArticles({
    domain: domain || undefined,
    q: debouncedSearch || undefined,
    page,
    size: 20,
  });


  const { mutate: triggerCollect, isPending: isCollecting } = useMutation({
    mutationFn: articlesApi.triggerCollection,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['articles'] });
    },
  });

  const handleDomainChange = useCallback((d: string) => {
    setDomain(d);
  }, [setDomain]);

  const articles = data?.content ?? [];
  const totalPages = data?.totalPages ?? 0;

  return (
    <div>
      {/* Page header */}
      <div className="page-header">
        <div>
          <h1 className="page-title">
            {domain === 'AI' ? '🤖 AI News' : domain === 'FINANCE' ? '📈 Finance News' : '🌐 Top Stories'}
          </h1>
          {data && (
            <p className="page-subtitle">
              {data.totalElements.toLocaleString()} articles
            </p>
          )}
        </div>
        <button
          id="refresh-btn"
          className="icon-btn"
          onClick={() => triggerCollect()}
          disabled={isCollecting}
          title="Refresh feeds now"
        >
          <RefreshCw size={18} className={isCollecting ? 'spinning' : ''} />
          {isCollecting ? 'Collecting...' : 'Refresh'}
        </button>
      </div>

      {/* Domain filter */}
      <FeedFilter domain={domain} onDomainChange={handleDomainChange} />

      {/* Article grid */}
      <ArticleList articles={articles} isLoading={isLoading} isFetching={isFetching && !isLoading} />

      {/* Pagination */}
      {totalPages > 1 && (
        <div className="pagination">
          <button
            className="page-btn"
            disabled={page === 0}
            onClick={() => setPage(page - 1)}
          >
            ← Previous
          </button>
          <span className="page-info">Page {page + 1} of {totalPages}</span>
          <button
            className="page-btn"
            disabled={!data?.hasNext}
            onClick={() => setPage(page + 1)}
          >
            Next →
          </button>
        </div>
      )}
    </div>
  );
};
