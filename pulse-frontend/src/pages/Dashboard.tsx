import React, { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import axios from 'axios';
import { ArticleCard, Article } from '../components/article/ArticleCard';

const mockArticles: Article[] = [
  {
    id: 1,
    title: "OpenAI releases GPT-5 with native multimodal capabilities",
    description: "The new model features a massive context window and significantly reduced costs, pushing the boundaries of AI capabilities forward.",
    originalUrl: "https://openai.com/news",
    domain: "AI",
    source: { name: "OpenAI Blog" },
    publishedAt: new Date().toISOString(),
    thumbnailUrl: "https://images.unsplash.com/photo-1677442135136-760c813028c0?q=80&w=800&auto=format&fit=crop"
  },
  {
    id: 2,
    title: "Market hits record highs amid tech stock surge",
    description: "Major indices rally as semiconductor companies post better-than-expected earnings for Q3.",
    originalUrl: "https://finance.yahoo.com",
    domain: "FINANCE",
    source: { name: "Yahoo Finance" },
    publishedAt: new Date(Date.now() - 3600000).toISOString(),
    thumbnailUrl: "https://images.unsplash.com/photo-1611974789855-9c2a0a7236a3?q=80&w=800&auto=format&fit=crop"
  },
  {
    id: 3,
    title: "Anthropic's new Opus model beats benchmarks",
    description: "Claude 3.5 Opus shows unprecedented reasoning capabilities across various academic benchmarks.",
    originalUrl: "https://anthropic.com",
    domain: "AI",
    source: { name: "Anthropic Blog" },
    publishedAt: new Date(Date.now() - 7200000).toISOString(),
    thumbnailUrl: "https://images.unsplash.com/photo-1620712948343-0008eccfc1d7?q=80&w=800&auto=format&fit=crop"
  }
];

export const Dashboard = () => {
  const [domain, setDomain] = useState<string>('');

  const { data: articles, isLoading } = useQuery({
    queryKey: ['articles', domain],
    queryFn: async () => {
      try {
        const res = await axios.get(`http://localhost:8080/api/v1/articles`, {
          params: { domain: domain || undefined }
        });
        if (res.data && res.data.length > 0) return res.data;
        return mockArticles.filter(a => domain ? a.domain === domain : true);
      } catch (err) {
        return mockArticles.filter(a => domain ? a.domain === domain : true);
      }
    }
  });

  return (
    <div>
      <div style={{ display: 'flex', gap: '1rem', marginBottom: '2rem', alignItems: 'center' }}>
        <h2 style={{ fontSize: '1.5rem', fontWeight: 600 }}>Top Stories</h2>
        <div style={{ display: 'flex', gap: '0.5rem' }}>
          <button 
            className={`badge ${domain === '' ? 'active' : ''}`} 
            style={{ cursor: 'pointer', background: domain === '' ? 'var(--accent)' : 'var(--bg-secondary)', color: domain === '' ? '#fff' : 'var(--text-secondary)' }}
            onClick={() => setDomain('')}
          >
            All
          </button>
          <button 
            className={`badge ${domain === 'AI' ? 'active' : ''}`} 
            style={{ cursor: 'pointer', background: domain === 'AI' ? 'var(--accent)' : 'var(--bg-secondary)', color: domain === 'AI' ? '#fff' : 'var(--text-secondary)' }}
            onClick={() => setDomain('AI')}
          >
            AI
          </button>
          <button 
            className={`badge ${domain === 'FINANCE' ? 'active' : ''}`} 
            style={{ cursor: 'pointer', background: domain === 'FINANCE' ? 'var(--accent)' : 'var(--bg-secondary)', color: domain === 'FINANCE' ? '#fff' : 'var(--text-secondary)' }}
            onClick={() => setDomain('FINANCE')}
          >
            Finance
          </button>
        </div>
      </div>

      {isLoading ? (
        <div className="grid-articles">
          {[1,2,3].map(i => <div key={i} className="card" style={{ height: '350px', background: 'var(--bg-secondary)', animation: 'pulse 2s infinite' }} />)}
        </div>
      ) : (
        <div className="grid-articles">
          {articles?.map((article: Article) => (
            <ArticleCard key={article.id} article={article} />
          ))}
        </div>
      )}
    </div>
  );
};
