import React, { useState } from 'react';
import { formatDistanceToNow } from 'date-fns';
import { ExternalLink, Bot } from 'lucide-react';
import type { Article } from '../../api/types';
import { AiSummaryPanel } from './AiSummaryPanel';
import { BookmarkButton } from './BookmarkButton';

// Re-export Article type for backward compatibility
export type { Article };

const FALLBACK_IMG = 'https://images.unsplash.com/photo-1678107567885-3b98ea834466?q=80&w=800&auto=format&fit=crop';

export const ArticleCard = ({ article }: { article: Article }) => {
  const [aiOpen, setAiOpen] = useState(false);

  const timeAgo = article.publishedAt
    ? formatDistanceToNow(new Date(article.publishedAt), { addSuffix: true })
    : '';

  return (
    <div className="card" id={`article-card-${article.id}`}>
      {/* Clickable thumbnail + header → opens article */}
      <a
        href={article.originalUrl}
        target="_blank"
        rel="noreferrer"
        aria-label={article.title}
        className="card-link"
      >
        <img
          src={article.thumbnailUrl || FALLBACK_IMG}
          alt={article.title}
          className="card-img"
          onError={(e) => { (e.target as HTMLImageElement).src = FALLBACK_IMG; }}
        />
        <div className="card-content">
          <h3 className="card-title">{article.title}</h3>
          {article.description && (
            <p className="card-desc">{article.description}</p>
          )}
        </div>
      </a>

      {/* Card footer */}
      <div className="card-footer">
        <div className="card-meta">
          <div className="card-source">
            <span className="card-source-name">
              {article.source?.name ?? article.domain}
            </span>
            {timeAgo && (
              <>
                <span>·</span>
                <span>{timeAgo}</span>
              </>
            )}
          </div>
          <div className="card-actions">
            {/* Bookmark toggle */}
            <BookmarkButton articleId={article.id} compact />
            {/* AI Summary toggle */}
            <button
              id={`ai-toggle-${article.id}`}
              className={`card-ai-btn ${aiOpen ? 'active' : ''}`}
              onClick={() => setAiOpen((v) => !v)}
              title="AI Summary"
              aria-expanded={aiOpen}
            >
              <Bot size={14} />
            </button>
            {/* Open original */}
            <a
              href={article.originalUrl}
              target="_blank"
              rel="noreferrer"
              className="card-ai-btn"
              title="Read original"
            >
              <ExternalLink size={14} />
            </a>
          </div>
        </div>

        {/* Expandable AI Summary Panel */}
        {aiOpen && (
          <div className="card-ai-expand">
            <AiSummaryPanel articleId={article.id} />
          </div>
        )}
      </div>
    </div>
  );
};
