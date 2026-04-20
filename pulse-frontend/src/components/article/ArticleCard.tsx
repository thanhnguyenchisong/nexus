import React from 'react';
import { formatDistanceToNow } from 'date-fns';
import { ExternalLink } from 'lucide-react';

export interface Article {
  id: number;
  title: string;
  description: string;
  originalUrl: string;
  thumbnailUrl?: string;
  source: { name: string; iconUrl?: string; domain?: string; };
  domain: string;
  publishedAt: string;
}

export const ArticleCard = ({ article }: { article: Article }) => {
  return (
    <a href={article.originalUrl} target="_blank" rel="noreferrer" className="card group">
      <img 
        src={article.thumbnailUrl || 'https://images.unsplash.com/photo-1678107567885-3b98ea834466?q=80&w=800&auto=format&fit=crop'} 
        alt={article.title} 
        className="card-img" 
      />
      <div className="card-content">
        <h3 className="card-title group-hover:text-accent transition-colors">{article.title}</h3>
        {article.description && (
          <p className="text-secondary" style={{ fontSize: '0.875rem', marginBottom: '1rem', display: '-webkit-box', WebkitLineClamp: 2, WebkitBoxOrient: 'vertical', overflow: 'hidden' }}>
            {article.description}
          </p>
        )}
        <div className="card-meta">
          <div className="card-source">
             <span style={{ fontWeight: 600, color: 'var(--text-primary)' }}>{article.domain || 'AI'}</span>
             <span>•</span>
             <span>{formatDistanceToNow(new Date(article.publishedAt), { addSuffix: true })}</span>
          </div>
          <ExternalLink size={16} />
        </div>
      </div>
    </a>
  );
};
