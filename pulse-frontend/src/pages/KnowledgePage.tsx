import React, { useState } from 'react';
import { Bookmark, BookmarkCheck, FolderOpen, Trash2, FileEdit, Plus, Loader2 } from 'lucide-react';
import {
  useBookmarks,
  useUpdateBookmark,
  useDeleteBookmark,
  useCollections,
  useCreateCollection,
  useDeleteCollection,
} from '../hooks/useKnowledge';
import type { Bookmark as BookmarkType } from '../api/types';
import { formatDistanceToNow } from 'date-fns';

const STATUS_TABS = [
  { value: '',         label: 'All',      icon: <Bookmark size={14} /> },
  { value: 'UNREAD',   label: 'Unread',   icon: <BookmarkCheck size={14} /> },
  { value: 'READ',     label: 'Read',     icon: <BookmarkCheck size={14} /> },
  { value: 'ARCHIVED', label: 'Archived', icon: <FolderOpen size={14} /> },
];

const STATUS_BADGE: Record<string, { label: string; cls: string }> = {
  UNREAD:   { label: 'Unread',   cls: 'status-badge-unread' },
  READ:     { label: 'Read',     cls: 'status-badge-read' },
  ARCHIVED: { label: 'Archived', cls: 'status-badge-archived' },
};

export const KnowledgePage = () => {
  const [activeTab, setActiveTab] = useState<'bookmarks' | 'collections'>('bookmarks');
  const [statusFilter, setStatusFilter] = useState('');
  const [editingNote, setEditingNote] = useState<{ id: number; note: string } | null>(null);
  const [newCollectionName, setNewCollectionName] = useState('');
  const [showNewCollection, setShowNewCollection] = useState(false);

  const { data: bookmarks, isLoading: bLoading } = useBookmarks(statusFilter || undefined);
  const { data: collections, isLoading: cLoading } = useCollections();
  const { mutate: updateBookmark } = useUpdateBookmark();
  const { mutate: deleteBookmark, isPending: isDeleting } = useDeleteBookmark();
  const { mutate: createCollection, isPending: isCreating } = useCreateCollection();
  const { mutate: deleteCollection } = useDeleteCollection();

  const handleStatusChange = (bookmarkId: number, status: string) => {
    updateBookmark({ id: bookmarkId, data: { status: status as 'READ' | 'UNREAD' | 'ARCHIVED' } });
  };

  const handleSaveNote = (bookmarkId: number) => {
    if (!editingNote) return;
    updateBookmark({ id: bookmarkId, data: { userNote: editingNote.note } });
    setEditingNote(null);
  };

  const handleCreateCollection = () => {
    if (!newCollectionName.trim()) return;
    createCollection(
      { name: newCollectionName.trim() },
      { onSuccess: () => { setNewCollectionName(''); setShowNewCollection(false); } }
    );
  };

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">📚 Knowledge Base</h1>
          <p className="page-subtitle">Your saved articles, notes, and collections</p>
        </div>
      </div>

      {/* Tab switcher */}
      <div className="kb-tabs">
        <button
          id="tab-bookmarks"
          className={`kb-tab ${activeTab === 'bookmarks' ? 'active' : ''}`}
          onClick={() => setActiveTab('bookmarks')}
        >
          <Bookmark size={16} /> Bookmarks
          {bookmarks && <span className="kb-tab-count">{bookmarks.length}</span>}
        </button>
        <button
          id="tab-collections"
          className={`kb-tab ${activeTab === 'collections' ? 'active' : ''}`}
          onClick={() => setActiveTab('collections')}
        >
          <FolderOpen size={16} /> Collections
          {collections && <span className="kb-tab-count">{collections.length}</span>}
        </button>
      </div>

      {/* ── BOOKMARKS TAB ── */}
      {activeTab === 'bookmarks' && (
        <>
          {/* Status filter */}
          <div className="feed-filter" style={{ marginBottom: '1.25rem' }}>
            {STATUS_TABS.map((t) => (
              <button
                key={t.value}
                id={`bookmark-status-${t.value || 'all'}`}
                className={`filter-btn ${statusFilter === t.value ? 'active' : ''}`}
                onClick={() => setStatusFilter(t.value)}
              >
                {t.icon} {t.label}
              </button>
            ))}
          </div>

          {bLoading ? (
            <div className="kb-loading"><Loader2 size={20} className="spinning" /> Loading bookmarks...</div>
          ) : !bookmarks?.length ? (
            <div className="empty-state">
              <div className="empty-icon">🔖</div>
              <h3>No bookmarks yet</h3>
              <p>Click the bookmark icon on any article card to save it here.</p>
            </div>
          ) : (
            <div className="kb-list">
              {bookmarks.map((bm: BookmarkType) => {
                const badge = STATUS_BADGE[bm.status] ?? STATUS_BADGE.UNREAD;
                const timeAgo = formatDistanceToNow(new Date(bm.savedAt), { addSuffix: true });

                return (
                  <div key={bm.id} className="kb-item">
                    {/* Thumbnail */}
                    {bm.article.thumbnailUrl && (
                      <a href={bm.article.originalUrl} target="_blank" rel="noreferrer" className="kb-item-thumb">
                        <img
                          src={bm.article.thumbnailUrl}
                          alt={bm.article.title}
                          onError={(e) => { (e.currentTarget as HTMLImageElement).style.display = 'none'; }}
                        />
                      </a>
                    )}

                    <div className="kb-item-body">
                      {/* Header */}
                      <div className="kb-item-header">
                        <a
                          href={bm.article.originalUrl}
                          target="_blank"
                          rel="noreferrer"
                          className="kb-item-title"
                        >
                          {bm.article.title}
                        </a>
                        <span className={`kb-status-badge ${badge.cls}`}>{badge.label}</span>
                      </div>

                      {/* Meta */}
                      <div className="kb-item-meta">
                        <span>{bm.article.source?.name ?? bm.article.domain}</span>
                        <span>·</span>
                        <span>{timeAgo}</span>
                      </div>

                      {/* Note editor */}
                      {editingNote?.id === bm.id ? (
                        <div className="kb-note-editor">
                          <textarea
                            className="kb-note-textarea"
                            value={editingNote.note}
                            onChange={(e) => setEditingNote({ id: bm.id, note: e.target.value })}
                            placeholder="Write a note..."
                            rows={3}
                          />
                          <div className="kb-note-actions">
                            <button className="kb-btn kb-btn-primary" onClick={() => handleSaveNote(bm.id)}>
                              Save
                            </button>
                            <button className="kb-btn" onClick={() => setEditingNote(null)}>
                              Cancel
                            </button>
                          </div>
                        </div>
                      ) : bm.userNote ? (
                        <div className="kb-note" onClick={() => setEditingNote({ id: bm.id, note: bm.userNote ?? '' })}>
                          <FileEdit size={12} />
                          <span>{bm.userNote}</span>
                        </div>
                      ) : null}

                      {/* Actions */}
                      <div className="kb-item-actions">
                        {/* Status quick-change */}
                        <select
                          className="kb-select"
                          value={bm.status}
                          onChange={(e) => handleStatusChange(bm.id, e.target.value)}
                          id={`status-select-${bm.id}`}
                        >
                          <option value="UNREAD">Unread</option>
                          <option value="READ">Read</option>
                          <option value="ARCHIVED">Archived</option>
                        </select>

                        {/* Add note */}
                        {!editingNote && (
                          <button
                            className="kb-btn"
                            id={`edit-note-${bm.id}`}
                            onClick={() => setEditingNote({ id: bm.id, note: bm.userNote ?? '' })}
                          >
                            <FileEdit size={13} />
                            {bm.userNote ? 'Edit note' : 'Add note'}
                          </button>
                        )}

                        {/* Delete */}
                        <button
                          className="kb-btn kb-btn-danger"
                          id={`delete-bm-${bm.id}`}
                          onClick={() => deleteBookmark(bm.id)}
                          disabled={isDeleting}
                        >
                          <Trash2 size={13} />
                          Remove
                        </button>
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>
          )}
        </>
      )}

      {/* ── COLLECTIONS TAB ── */}
      {activeTab === 'collections' && (
        <>
          <div style={{ display: 'flex', justifyContent: 'flex-end', marginBottom: '1rem' }}>
            <button
              id="new-collection-btn"
              className="icon-btn"
              onClick={() => setShowNewCollection(true)}
            >
              <Plus size={16} /> New Collection
            </button>
          </div>

          {showNewCollection && (
            <div className="kb-new-collection">
              <input
                id="collection-name-input"
                className="kb-input"
                placeholder="Collection name..."
                value={newCollectionName}
                onChange={(e) => setNewCollectionName(e.target.value)}
                onKeyDown={(e) => e.key === 'Enter' && handleCreateCollection()}
                autoFocus
              />
              <button className="kb-btn kb-btn-primary" onClick={handleCreateCollection} disabled={isCreating}>
                {isCreating ? <Loader2 size={14} className="spinning" /> : <Plus size={14} />}
                Create
              </button>
              <button className="kb-btn" onClick={() => { setShowNewCollection(false); setNewCollectionName(''); }}>
                Cancel
              </button>
            </div>
          )}

          {cLoading ? (
            <div className="kb-loading"><Loader2 size={20} className="spinning" /> Loading collections...</div>
          ) : !collections?.length ? (
            <div className="empty-state">
              <div className="empty-icon">📁</div>
              <h3>No collections yet</h3>
              <p>Create a collection to organise your bookmarks into topics.</p>
            </div>
          ) : (
            <div className="sources-grid">
              {collections.map((col) => (
                <div key={col.id} className="source-card">
                  <div className="source-card-header">
                    <div className="source-icon">
                      <FolderOpen size={20} />
                    </div>
                    <div className="source-info">
                      <div className="source-name">{col.name}</div>
                      <div className="source-domain-badge">
                        {col.articleCount} article{col.articleCount !== 1 ? 's' : ''}
                      </div>
                    </div>
                  </div>
                  {col.description && (
                    <p style={{ fontSize: '0.8125rem', color: 'var(--text-secondary)', lineHeight: 1.5 }}>
                      {col.description}
                    </p>
                  )}
                  <div className="source-actions">
                    <button
                      className="source-action-btn kb-btn-danger"
                      id={`delete-col-${col.id}`}
                      onClick={() => deleteCollection(col.id)}
                    >
                      <Trash2 size={13} /> Delete
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </>
      )}
    </div>
  );
};
