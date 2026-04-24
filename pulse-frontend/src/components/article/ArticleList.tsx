import React from 'react';
import { ArticleCard } from './ArticleCard';
import type { Article } from '../../api/types';

const SkeletonCard = () => (
  <div className="card skeleton-card" aria-hidden="true">
    <div className="skeleton skeleton-img" />
    <div className="card-content">
      <div className="skeleton skeleton-line" style={{ width: '90%', height: '1rem', marginBottom: '0.5rem' }} />
      <div className="skeleton skeleton-line" style={{ width: '70%', height: '1rem', marginBottom: '0.5rem' }} />
      <div className="skeleton skeleton-line" style={{ width: '50%', height: '0.75rem', marginTop: 'auto' }} />
    </div>
  </div>
);

interface ArticleListProps {
  articles?: Article[];
  isLoading: boolean;
  isFetching?: boolean;
}

export const ArticleList = ({ articles, isLoading, isFetching }: ArticleListProps) => {
  if (isLoading) {
    return (
      <div className="grid-articles">
        {[1, 2, 3, 4, 5, 6].map((i) => <SkeletonCard key={i} />)}
      </div>
    );
  }

  if (!articles || articles.length === 0) {
    return (
      <div className="empty-state">
        <div className="empty-icon">📰</div>
        <h3>No articles yet</h3>
        <p>Articles will appear here once the feed collector runs. Check back soon!</p>
      </div>
    );
  }

  return (
    <div className={`grid-articles ${isFetching ? 'fetching' : ''}`}>
      {articles.map((article) => (
        <ArticleCard key={article.id} article={article} />
      ))}
    </div>
  );
};
