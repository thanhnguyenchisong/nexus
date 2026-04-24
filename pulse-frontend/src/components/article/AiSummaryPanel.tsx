import React from 'react';
import { Bot, Sparkles, Tag, Loader2 } from 'lucide-react';
import { SentimentBadge } from './SentimentBadge';
import { useAiSummary, useGenerateSummary } from '../../hooks/useAiSummary';

interface AiSummaryPanelProps {
  articleId: number;
  /** Compact mode: just show a "Get AI Summary" button, expand on click */
  compact?: boolean;
}

export const AiSummaryPanel = ({ articleId }: AiSummaryPanelProps) => {
  const { data: summary, isLoading, isError } = useAiSummary(articleId);
  const { mutate: generate, isPending: isGenerating } = useGenerateSummary();

  // Has no summary yet
  if (!isLoading && (isError || !summary)) {
    return (
      <div className="ai-panel ai-panel-empty">
        <Bot size={16} />
        <span>No AI summary yet</span>
        <button
          id={`generate-summary-${articleId}`}
          className="ai-generate-btn"
          onClick={() => generate(articleId)}
          disabled={isGenerating}
        >
          {isGenerating ? (
            <><Loader2 size={14} className="spinning" /> Generating...</>
          ) : (
            <><Sparkles size={14} /> Generate</>
          )}
        </button>
      </div>
    );
  }

  if (isLoading) {
    return (
      <div className="ai-panel ai-panel-loading">
        <Loader2 size={14} className="spinning" />
        <span>Loading summary...</span>
      </div>
    );
  }

  if (!summary) return null;

  return (
    <div className="ai-panel">
      {/* Header */}
      <div className="ai-panel-header">
        <div className="ai-panel-title">
          <Bot size={14} />
          <span>AI Summary</span>
          <span className="ai-provider-badge">{summary.aiProvider}</span>
        </div>
        <SentimentBadge sentiment={summary.sentiment} score={summary.sentimentScore} compact />
      </div>

      {/* Summary text */}
      <p className="ai-panel-summary">{summary.summary}</p>

      {/* Key insights */}
      {summary.keyInsights.length > 0 && (
        <div className="ai-insights">
          <div className="ai-insights-title">
            <Sparkles size={12} /> Key Insights
          </div>
          <ul className="ai-insights-list">
            {summary.keyInsights.map((insight, i) => (
              <li key={i}>{insight}</li>
            ))}
          </ul>
        </div>
      )}

      {/* Tags */}
      {summary.tags.length > 0 && (
        <div className="ai-tags">
          <Tag size={12} />
          {summary.tags.map((tag) => (
            <span key={tag} className="ai-tag">{tag}</span>
          ))}
        </div>
      )}
    </div>
  );
};
