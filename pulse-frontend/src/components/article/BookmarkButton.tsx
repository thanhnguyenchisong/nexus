import React from 'react';
import { Bookmark, BookmarkCheck, Loader2 } from 'lucide-react';
import {
  useBookmarkByArticle,
  useCreateBookmark,
  useDeleteBookmarkByArticle,
} from '../../hooks/useKnowledge';

interface BookmarkButtonProps {
  articleId: number;
  compact?: boolean;
}

export const BookmarkButton = ({ articleId, compact = false }: BookmarkButtonProps) => {
  const { data: bookmark, isLoading } = useBookmarkByArticle(articleId);
  const { mutate: createBookmark, isPending: isCreating } = useCreateBookmark();
  const { mutate: removeBookmark, isPending: isRemoving } = useDeleteBookmarkByArticle();

  const isBookmarked = !!bookmark;
  const isBusy = isLoading || isCreating || isRemoving;

  const handleToggle = (e: React.MouseEvent) => {
    e.preventDefault();
    e.stopPropagation();
    if (isBusy) return;

    if (isBookmarked) {
      removeBookmark(articleId);
    } else {
      createBookmark({ articleId });
    }
  };

  return (
    <button
      id={`bookmark-${articleId}`}
      className={`card-ai-btn ${isBookmarked ? 'active' : ''}`}
      onClick={handleToggle}
      disabled={isBusy}
      title={isBookmarked ? 'Remove bookmark' : 'Save for later'}
      style={isBookmarked ? { color: '#f59e0b', borderColor: '#f59e0b' } : undefined}
    >
      {isBusy
        ? <Loader2 size={14} className="spinning" />
        : isBookmarked
          ? <BookmarkCheck size={14} />
          : <Bookmark size={14} />
      }
      {!compact && (
        <span style={{ fontSize: '0.75rem' }}>
          {isBookmarked ? 'Saved' : 'Save'}
        </span>
      )}
    </button>
  );
};
