// Shared API types matching backend DTOs

export interface SourceSummary {
  id: number;
  name: string;
  iconUrl?: string;
  domain: string;
}

export interface Article {
  id: number;
  title: string;
  description?: string;
  originalUrl: string;
  author?: string;
  thumbnailUrl?: string;
  domain: string;
  category?: string;
  language: string;
  publishedAt?: string;
  createdAt?: string;
  source?: SourceSummary;
}

export interface Source {
  id: number;
  name: string;
  url: string;
  feedUrl: string;
  iconUrl?: string;
  type: string;
  domain: string;
  language: string;
  status: string;
  fetchIntervalMinutes: number;
  errorCount: number;
  lastFetchedAt?: string;
}

export interface PagedResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  hasNext: boolean;
}

export interface ArticleFilters {
  domain?: string;
  language?: string;
  category?: string;
  q?: string;
  page?: number;
  size?: number;
}

export interface AiSummary {
  id: number;
  articleId: number;
  summary: string;
  keyInsights: string[];
  sentiment: 'POSITIVE' | 'NEGATIVE' | 'NEUTRAL';
  sentimentScore: number;
  tags: string[];
  aiProvider: string;
  modelUsed: string;
  generatedAt: string;
}

export interface Bookmark {
  id: number;
  article: Article;
  userNote?: string;
  status: 'READ' | 'UNREAD' | 'ARCHIVED';
  savedAt: string;
  updatedAt: string;
}

export interface BookmarkFormData {
  articleId: number;
  userNote?: string;
}

export interface BookmarkUpdateData {
  userNote?: string;
  status?: 'READ' | 'UNREAD' | 'ARCHIVED';
}

export interface Collection {
  id: number;
  name: string;
  description?: string;
  articleCount: number;
  createdAt: string;
}


