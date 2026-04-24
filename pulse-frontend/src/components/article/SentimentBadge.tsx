import React from 'react';

type Sentiment = 'POSITIVE' | 'NEGATIVE' | 'NEUTRAL';

interface SentimentBadgeProps {
  sentiment: Sentiment;
  score?: number;
  compact?: boolean;
}

const SENTIMENT_CONFIG: Record<Sentiment, { label: string; emoji: string; className: string }> = {
  POSITIVE: { label: 'Positive', emoji: '📈', className: 'sentiment-positive' },
  NEGATIVE: { label: 'Negative', emoji: '📉', className: 'sentiment-negative' },
  NEUTRAL:  { label: 'Neutral',  emoji: '⚖️', className: 'sentiment-neutral'  },
};

export const SentimentBadge = ({ sentiment, score, compact = false }: SentimentBadgeProps) => {
  const config = SENTIMENT_CONFIG[sentiment] ?? SENTIMENT_CONFIG.NEUTRAL;
  const pct = score != null ? Math.round(Math.abs(score) * 100) : null;

  return (
    <span className={`sentiment-badge ${config.className}`} title={`Sentiment score: ${score?.toFixed(2) ?? 'N/A'}`}>
      <span>{config.emoji}</span>
      {!compact && <span>{config.label}</span>}
      {!compact && pct != null && <span className="sentiment-pct">{pct}%</span>}
    </span>
  );
};
