import React from 'react';
import { Home, Layers, Hash, Moon, Sun, Search, Settings } from 'lucide-react';
import { useThemeStore } from '../../store/themeStore';

export const AppLayout = ({ children }: { children: React.ReactNode }) => {
  const { theme, toggleTheme } = useThemeStore();

  return (
    <>
      <header className="app-header">
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', fontWeight: 700, fontSize: '1.25rem', color: 'var(--accent)' }}>
          <Layers />
          Pulse
        </div>
        <div style={{ flex: 1, maxWidth: '400px', margin: '0 2rem' }}>
          <div style={{ position: 'relative', display: 'flex', alignItems: 'center' }}>
            <Search size={18} style={{ position: 'absolute', left: '1rem', color: 'var(--text-tertiary)' }} />
            <input 
              type="text" 
              placeholder="Search news, topics, sources..." 
              style={{
                width: '100%',
                padding: '0.5rem 1rem 0.5rem 2.5rem',
                borderRadius: '9999px',
                border: '1px solid var(--border)',
                background: 'var(--bg-secondary)',
                color: 'var(--text-primary)',
                outline: 'none'
              }} 
            />
          </div>
        </div>
        <button className="toggle-btn" onClick={toggleTheme}>
          {theme === 'dark' ? <Sun size={20} /> : <Moon size={20} />}
        </button>
      </header>
      
      <div className="main-layout">
        <aside className="sidebar">
          <nav>
            <a href="#" className="nav-link active">
              <Home size={20} /> My Feed
            </a>
            <a href="#" className="nav-link">
              <Hash size={20} /> Explore
            </a>
            <div style={{ margin: '2rem 0 1rem', fontSize: '0.75rem', fontWeight: 600, textTransform: 'uppercase', color: 'var(--text-tertiary)' }}>
              Sources
            </div>
            <a href="#" className="nav-link">
              <Settings size={20} /> Manage Sources
            </a>
          </nav>
        </aside>
        
        <main className="content-area">
          {children}
        </main>
      </div>
    </>
  );
};
