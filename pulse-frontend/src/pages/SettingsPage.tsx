import React from 'react';
import { Settings } from 'lucide-react';

export const SettingsPage = () => {
  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">⚙️ Settings</h1>
          <p className="page-subtitle">Preferences and configuration</p>
        </div>
      </div>

      <div style={{
        display: 'grid',
        gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))',
        gap: '1.5rem',
        marginTop: '1rem',
      }}>
        <SettingsCard
          title="Feed Collection"
          description="Configure how often Pulse fetches new articles from your sources."
          badge="Coming Soon"
        />
        <SettingsCard
          title="AI Summarization"
          description="Enable automatic AI-powered summaries using OpenAI or Claude."
          badge="Phase 2"
        />
        <SettingsCard
          title="Notifications"
          description="Get notified about trending stories or new articles from your favourite sources."
          badge="Phase 4"
        />
        <SettingsCard
          title="Data & Export"
          description="Export your bookmarks and notes as Markdown or PDF."
          badge="Phase 3"
        />
      </div>
    </div>
  );
};

const SettingsCard = ({
  title,
  description,
  badge,
}: {
  title: string;
  description: string;
  badge: string;
}) => (
  <div className="source-card" style={{ opacity: 0.75 }}>
    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
      <div className="source-name">{title}</div>
      <span className="badge" style={{ fontSize: '0.7rem', background: 'var(--border)', color: 'var(--text-tertiary)', whiteSpace: 'nowrap' }}>
        {badge}
      </span>
    </div>
    <p style={{ fontSize: '0.875rem', color: 'var(--text-secondary)', lineHeight: 1.6 }}>
      {description}
    </p>
  </div>
);
