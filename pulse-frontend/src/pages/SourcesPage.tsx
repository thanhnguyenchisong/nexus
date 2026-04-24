import React, { useState } from 'react';
import { useSources } from '../hooks/useSources';
import { Globe, AlertCircle, CheckCircle, Clock, ExternalLink, RefreshCw, ToggleLeft, ToggleRight } from 'lucide-react';
import { sourcesApi } from '../api/sources';
import { useMutation, useQueryClient } from '@tanstack/react-query';

const STATUS_ICON: Record<string, React.ReactNode> = {
  ACTIVE:   <CheckCircle size={14} className="status-active" />,
  ERROR:    <AlertCircle size={14} className="status-error" />,
  INACTIVE: <Clock size={14} className="status-inactive" />,
};

export const SourcesPage = () => {
  const [domain, setDomain] = useState('');
  const { data: sources, isLoading } = useSources(domain || undefined);
  const queryClient = useQueryClient();

  const { mutate: toggleSource, isPending: isToggling, variables: toggleVars } = useMutation({
    mutationFn: ({ id, status }: { id: number; status: string }) =>
      sourcesApi.updateSource(id, { status }),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['sources'] }),
  });

  const { mutate: fetchSource, isPending: isFetching, variables: fetchVars } = useMutation({
    mutationFn: (id: number) => sourcesApi.triggerFetch(id),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['articles'] }),
  });

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">📡 Feed Sources</h1>
          <p className="page-subtitle">
            {sources ? `${sources.length} sources configured` : 'All configured RSS feeds and APIs'}
          </p>
        </div>
      </div>

      {/* Domain filter */}
      <div className="feed-filter" style={{ marginBottom: '1.5rem' }}>
        {[
          { value: '', label: 'All' },
          { value: 'AI', label: '🤖 AI' },
          { value: 'FINANCE', label: '📈 Finance' },
        ].map((d) => (
          <button
            key={d.value}
            id={`sources-domain-${d.value || 'all'}`}
            className={`filter-btn ${domain === d.value ? 'active' : ''}`}
            onClick={() => setDomain(d.value)}
          >
            {d.label}
          </button>
        ))}
      </div>

      {isLoading ? (
        <div className="sources-grid">
          {[1, 2, 3, 4, 5, 6].map((i) => (
            <div key={i} className="source-card">
              <div className="skeleton" style={{ width: 40, height: 40, borderRadius: '50%', marginBottom: '0.75rem' }} />
              <div className="skeleton skeleton-line" style={{ width: '60%' }} />
            </div>
          ))}
        </div>
      ) : (
        <div className="sources-grid">
          {(sources ?? []).map((source) => {
            const isThisToggling = isToggling && toggleVars?.id === source.id;
            const isThisFetching = isFetching && fetchVars === source.id;

            return (
              <div key={source.id} className={`source-card ${source.status === 'INACTIVE' ? 'source-card-inactive' : ''}`}>
                <div className="source-card-header">
                  <div className="source-icon">
                    {source.iconUrl ? (
                      <img src={source.iconUrl} alt={source.name} width={24} height={24}
                        onError={(e) => { (e.target as HTMLImageElement).style.display = 'none'; }} />
                    ) : (
                      <Globe size={20} />
                    )}
                  </div>
                  <div className="source-info">
                    <div className="source-name">{source.name}</div>
                    <div className="source-domain-badge">{source.domain} · {source.language.toUpperCase()}</div>
                  </div>
                  <div className="source-status" title={source.status}>
                    {STATUS_ICON[source.status] ?? <Clock size={14} />}
                  </div>
                </div>

                <div className="source-meta">
                  <span>Interval: {source.fetchIntervalMinutes}m</span>
                  {source.lastFetchedAt && (
                    <>
                      <span>·</span>
                      <span title={source.lastFetchedAt}>
                        Last: {new Date(source.lastFetchedAt).toLocaleTimeString()}
                      </span>
                    </>
                  )}
                </div>

                {source.errorCount > 0 && (
                  <div className="source-error-badge">
                    ⚠ {source.errorCount} error{source.errorCount > 1 ? 's' : ''}
                  </div>
                )}

                <div className="source-actions">
                  {/* Toggle active/inactive */}
                  <button
                    id={`toggle-source-${source.id}`}
                    className="source-action-btn"
                    disabled={isThisToggling}
                    title={source.status === 'ACTIVE' ? 'Disable source' : 'Enable source'}
                    onClick={() =>
                      toggleSource({
                        id: source.id,
                        status: source.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE',
                      })
                    }
                  >
                    {source.status === 'ACTIVE'
                      ? <ToggleRight size={16} className="status-active" />
                      : <ToggleLeft size={16} className="status-inactive" />
                    }
                    {source.status === 'ACTIVE' ? 'Active' : 'Inactive'}
                  </button>

                  {/* Per-source fetch now */}
                  <button
                    id={`fetch-source-${source.id}`}
                    className="source-action-btn"
                    disabled={isThisFetching || source.status !== 'ACTIVE'}
                    title="Fetch now"
                    onClick={() => fetchSource(source.id)}
                  >
                    <RefreshCw size={14} className={isThisFetching ? 'spinning' : ''} />
                    Fetch
                  </button>

                  <a href={source.url} target="_blank" rel="noreferrer" className="source-action-btn source-link-btn">
                    <ExternalLink size={14} />
                    Visit
                  </a>
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
};
