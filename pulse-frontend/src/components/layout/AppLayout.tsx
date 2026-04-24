import React, { useCallback, useState } from 'react';
import { NavLink } from 'react-router-dom';
import { Home, Radio, Moon, Sun, Search, Menu, X, Settings, BookOpen } from 'lucide-react';
import { useThemeStore } from '../../store/themeStore';
import { useFilterStore } from '../../store/filterStore';

export const AppLayout = ({ children }: { children: React.ReactNode }) => {
  const { theme, toggleTheme } = useThemeStore();
  const { search, setSearch } = useFilterStore();
  const [sidebarOpen, setSidebarOpen] = useState(false);

  const handleSearch = useCallback((e: React.ChangeEvent<HTMLInputElement>) => {
    setSearch(e.target.value);
  }, [setSearch]);

  const closeSidebar = () => setSidebarOpen(false);

  return (
    <>
      <header className="app-header">
        {/* Hamburger — mobile only */}
        <button
          id="sidebar-toggle"
          className="hamburger"
          onClick={() => setSidebarOpen(true)}
          aria-label="Open menu"
        >
          <Menu size={22} />
        </button>

        <NavLink to="/" className="app-logo" onClick={closeSidebar}>
          <Radio size={22} />
          <span>Pulse</span>
        </NavLink>

        <div className="search-wrapper">
          <Search size={16} className="search-icon" />
          <input
            id="global-search"
            type="text"
            placeholder="Search news, topics..."
            className="search-input"
            value={search}
            onChange={handleSearch}
          />
        </div>

        <button id="theme-toggle" className="toggle-btn" onClick={toggleTheme} title="Toggle theme">
          {theme === 'dark' ? <Sun size={20} /> : <Moon size={20} />}
        </button>
      </header>

      {/* Mobile overlay */}
      {sidebarOpen && (
        <div
          className="sidebar-overlay"
          onClick={closeSidebar}
          aria-hidden="true"
        />
      )}

      <div className="main-layout">
        <aside className={`sidebar ${sidebarOpen ? 'sidebar-open' : ''}`}>
          <div className="sidebar-header">
            <span className="sidebar-brand">
              <Radio size={18} /> Pulse
            </span>
            <button
              id="sidebar-close"
              className="sidebar-close-btn"
              onClick={closeSidebar}
              aria-label="Close menu"
            >
              <X size={20} />
            </button>
          </div>

          <nav>
            <NavLink
              to="/"
              end
              className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}
              onClick={closeSidebar}
            >
              <Home size={18} /> My Feed
            </NavLink>
            <NavLink
              to="/knowledge"
              className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}
              onClick={closeSidebar}
            >
              <BookOpen size={18} /> Knowledge
            </NavLink>
            <NavLink
              to="/sources"
              className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}
              onClick={closeSidebar}
            >
              <Radio size={18} /> Sources
            </NavLink>
            <NavLink
              to="/settings"
              className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}
              onClick={closeSidebar}
            >
              <Settings size={18} /> Settings
            </NavLink>
          </nav>
        </aside>

        <main className="content-area">
          {children}
        </main>
      </div>
    </>
  );
};
