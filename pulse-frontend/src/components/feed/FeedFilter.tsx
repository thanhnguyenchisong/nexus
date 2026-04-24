import React from 'react';
import { Brain, TrendingUp, Globe } from 'lucide-react';

interface FeedFilterProps {
  domain: string;
  onDomainChange: (domain: string) => void;
}

const DOMAINS = [
  { value: '', label: 'All', icon: <Globe size={15} /> },
  { value: 'AI', label: 'AI', icon: <Brain size={15} /> },
  { value: 'FINANCE', label: 'Finance', icon: <TrendingUp size={15} /> },
];

export const FeedFilter = ({ domain, onDomainChange }: FeedFilterProps) => {
  return (
    <div className="feed-filter">
      {DOMAINS.map((d) => (
        <button
          key={d.value}
          id={`domain-filter-${d.value || 'all'}`}
          className={`filter-btn ${domain === d.value ? 'active' : ''}`}
          onClick={() => onDomainChange(d.value)}
        >
          {d.icon}
          {d.label}
        </button>
      ))}
    </div>
  );
};
