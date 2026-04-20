# Pulse — Personalized News Intelligence Platform

> **Status**: Plan Approved ✅  
> **Last Updated**: 2026-04-20  
> **Repo Structure**: Separate repos (backend / frontend)

---

## 1. Product Vision

**Pulse** là một nền tảng thu thập tin tức thông minh theo phong cách **MSN News**, tự động aggregate metadata từ nhiều nguồn trong các lĩnh vực người dùng quan tâm (hiện tại: **AI** và **Finance**). Hệ thống **không lưu nội dung bài viết** mà chỉ thu thập metadata (tiêu đề, mô tả ngắn, thumbnail, nguồn) và redirect người dùng đến bài viết gốc khi muốn đọc chi tiết.

### Vấn đề cần giải quyết
- Thông tin phân tán trên quá nhiều nguồn khác nhau
- Mất thời gian đọc và lọc tin quan trọng
- Không có nơi tập trung để lưu trữ và tìm kiếm lại kiến thức

### Giải pháp
- **Auto-aggregation**: Tự động crawl metadata từ RSS feeds, blogs, news APIs
- **AI Summary**: Tóm tắt nội dung từ description, phân loại tự động, highlight key insights
- **Personal Knowledge Base**: Bookmark, ghi chú, tag, và search
- **Zero Content Storage**: Chỉ lưu metadata → click mở bài gốc (giống MSN)

### Design Reference: MSN News
- Card-based layout với thumbnail + title + source + timestamp
- Click bất kỳ card nào → redirect đến URL gốc
- Category tabs để filter nhanh
- Minimal data storage, maximum content access

---

## 2. Confirmed Decisions

| Quyết định | Lựa chọn |
|------------|----------|
| **Tên dự án** | Pulse |
| **Backend** | Java Quarkus 3.x |
| **Frontend** | React + Vite + TypeScript |
| **Database** | PostgreSQL 16+ |
| **AI Provider** | Cả hai: OpenAI + Claude (với fallback) |
| **Dark Mode** | Có, từ Phase 1 |
| **Vietnamese Sources** | Chỉ Finance (CafeF, VnExpress Finance) |
| **Authentication** | Không cần Phase 1 (open access) |
| **Repo Structure** | Separate repos (pulse-backend, pulse-frontend) |
| **User Model** | Single user cho MVP |
| **Approach** | MVP-first, iterate qua 4 phases |
| **Content Strategy** | Metadata-only (MSN-style) — không lưu full content |
| **Article Click** | Redirect đến bài viết gốc (original URL) |
| **DB Optimization** | Lưu tối thiểu: title + description + thumbnail + URL |

---

## 3. Tech Stack Chi Tiết

### 3.1 Backend — Java Quarkus 3.x

| Component | Technology |
|-----------|-----------|
| **Framework** | Quarkus 3.x (Supersonic Subatomic Java) |
| **RSS Parsing** | Apache Camel Quarkus RSS Extension (`camel-quarkus-rss`) |
| **Scheduling** | Quarkus Scheduler / Quarkus Quartz |
| **HTTP Client** | Quarkus REST Client Reactive (non-blocking) |
| **ORM** | Hibernate ORM with Panache |
| **Migration** | Flyway |
| **Validation** | Hibernate Validator |
| **API Docs** | SmallRye OpenAPI (Swagger UI) |
| **Health** | SmallRye Health |
| **Metrics** | Micrometer + Prometheus |

**Quarkus Dependencies (pom.xml):**
```xml
<!-- Core -->
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-rest-jackson</artifactId>
</dependency>
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-hibernate-orm-panache</artifactId>
</dependency>
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-jdbc-postgresql</artifactId>
</dependency>
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-flyway</artifactId>
</dependency>

<!-- RSS & HTTP -->
<dependency>
    <groupId>org.apache.camel.quarkus</groupId>
    <artifactId>camel-quarkus-rss</artifactId>
</dependency>
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-rest-client-reactive-jackson</artifactId>
</dependency>

<!-- Scheduling -->
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-scheduler</artifactId>
</dependency>

<!-- Validation & API Docs -->
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-hibernate-validator</artifactId>
</dependency>
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-smallrye-openapi</artifactId>
</dependency>

<!-- Health & Metrics -->
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-smallrye-health</artifactId>
</dependency>
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-micrometer-registry-prometheus</artifactId>
</dependency>
```

### 3.2 Frontend — React + Vite + TypeScript

| Component | Technology |
|-----------|-----------|
| **Framework** | React 19 |
| **Build Tool** | Vite 6.x |
| **Language** | TypeScript 5.x |
| **Routing** | React Router v7 |
| **State Mgmt** | Zustand (lightweight) |
| **Data Fetching** | TanStack Query (React Query) |
| **HTTP Client** | Axios |
| **UI Components** | Custom + Radix UI primitives |
| **Icons** | Lucide React |
| **Charts** | Recharts |
| **Date** | date-fns |

### 3.3 Database — PostgreSQL 16+ (Optimized Minimal Storage)

| Component | Technology |
|-----------|-----------|
| **Primary DB** | PostgreSQL 16+ |
| **Storage Strategy** | Metadata-only (NO full article content) |
| **Full-text Search** | tsvector / tsquery on title + description (MVP) |
| **Vector Store** | pgvector (Phase 2 — embeddings for similarity) |
| **Migration** | Flyway |
| **Dev Mode** | Quarkus Dev Services (auto-starts PostgreSQL container) |
| **Est. Storage** | ~50MB/year (metadata-only vs ~500MB with full content) |

### 3.4 AI Integration — Dual Provider (OpenAI + Claude)

| Feature | Primary | Fallback | Model |
|---------|---------|----------|-------|
| **Summarization** | OpenAI | Claude | GPT-4o-mini / Claude 3.5 Haiku |
| **Key Insights** | OpenAI | Claude | GPT-4o-mini / Claude 3.5 Haiku |
| **Auto-tagging** | OpenAI | Claude | GPT-4o-mini / Claude 3.5 Haiku |
| **Sentiment** | OpenAI | Claude | GPT-4o-mini / Claude 3.5 Haiku |
| **Classification** | Local | — | Embeddings + pgvector |

**Fallback Strategy:**
```
Request → OpenAI API
           ├── Success → Return result
           └── Fail (timeout/rate-limit/error)
                └── Claude API
                     ├── Success → Return result
                     └── Fail → Queue for retry + log error
```

**Estimated Cost:** ~$5-15/tháng cho ~1000 articles/tháng

---

## 4. Data Sources

### 4.1 AI Domain (International)

| # | Nguồn | Loại | Priority | Feed URL |
|---|-------|------|----------|----------|
| 1 | OpenAI Blog | RSS | 🔴 High | `https://openai.com/blog/rss.xml` |
| 2 | Google AI Blog | RSS | 🔴 High | `https://blog.google/technology/ai/rss/` |
| 3 | DeepMind Blog | RSS | 🔴 High | `https://deepmind.google/blog/rss.xml` |
| 4 | MIT Tech Review | RSS | 🔴 High | `https://www.technologyreview.com/feed/` |
| 5 | Hugging Face Blog | RSS | 🟡 Medium | `https://huggingface.co/blog/feed.xml` |
| 6 | arXiv cs.AI | RSS | 🟡 Medium | `http://arxiv.org/rss/cs.AI` |
| 7 | VentureBeat AI | RSS | 🟡 Medium | `https://venturebeat.com/category/ai/feed/` |
| 8 | Hacker News | API | 🟡 Medium | `https://hacker-news.firebaseio.com/v0/` |
| 9 | MarkTechPost | RSS | 🟢 Low | `https://www.marktechpost.com/feed/` |
| 10 | The Verge AI | RSS | 🟢 Low | `https://www.theverge.com/rss/ai-artificial-intelligence/index.xml` |

### 4.2 Finance Domain (International)

| # | Nguồn | Loại | Priority | Feed URL |
|---|-------|------|----------|----------|
| 1 | Reuters Business | RSS | 🔴 High | `https://www.reuters.com/business/rss` |
| 2 | MarketWatch | RSS | 🔴 High | `https://feeds.marketwatch.com/marketwatch/topstories/` |
| 3 | CNBC | RSS | 🔴 High | `https://www.cnbc.com/id/100003114/device/rss/rss.html` |
| 4 | Finnhub | API | 🔴 High | `https://finnhub.io/api/v1/news` |
| 5 | Bloomberg | RSS | 🟡 Medium | Limited free access |
| 6 | Seeking Alpha | RSS | 🟡 Medium | `https://seekingalpha.com/market_currents.xml` |
| 7 | Investopedia | RSS | 🟡 Medium | `https://www.investopedia.com/feedbuilder/feed/getfeed/?feedName=rss_articles` |
| 8 | CoinDesk | RSS | 🟡 Medium | `https://www.coindesk.com/arc/outboundfeeds/rss/` |
| 9 | Yahoo Finance | RSS | 🟢 Low | `https://finance.yahoo.com/news/rssindex` |

### 4.3 Finance Domain (Vietnamese 🇻🇳)

| # | Nguồn | Loại | Priority | Feed URL |
|---|-------|------|----------|----------|
| 1 | CafeF | RSS | 🔴 High | `https://cafef.vn/rss/trang-chu.rss` |
| 2 | VnExpress Kinh Doanh | RSS | 🔴 High | `https://vnexpress.net/rss/kinh-doanh.rss` |
| 3 | VnExpress Tài Chính | RSS | 🔴 High | `https://vnexpress.net/rss/tai-chinh.rss` |
| 4 | VietStock | RSS | 🟡 Medium | `https://vietstock.vn/rss/tai-chinh.rss` |
| 5 | Thanh Niên Kinh Tế | RSS | 🟡 Medium | `https://thanhnien.vn/rss/kinh-te.rss` |
| 6 | Tuổi Trẻ Kinh Doanh | RSS | 🟢 Low | `https://tuoitre.vn/rss/kinh-doanh.rss` |

> **Tổng cộng: 25 nguồn** (10 AI + 9 Finance Intl + 6 Finance VN)

---

## 5. System Architecture

### 5.1 High-Level Architecture

```
┌──────────────────────────────────────────────────────────────────────┐
│                          PULSE PLATFORM                              │
│                                                                      │
│  ┌──────────────┐                                  ┌──────────────┐  │
│  │  pulse-       │                                  │ pulse-        │  │
│  │  frontend     │◄──── REST API (JSON) ──────────►│ backend       │  │
│  │              │                                  │              │  │
│  │  React+Vite  │    ┌──────────────────────┐      │  Quarkus     │  │
│  │  TypeScript  │    │    PostgreSQL 16+     │      │  Java 21     │  │
│  │  Dark Mode   │    │    + pgvector         │      │              │  │
│  │  Port: 3000  │    │    Port: 5432         │      │  Port: 8080  │  │
│  └──────────────┘    └──────────┬───────────┘      └──────┬───────┘  │
│                                 │                         │          │
│                                 │    ┌────────────────────┘          │
│                                 │    │                               │
│                                 │    ▼                               │
│                          ┌──────┴────────────┐                       │
│                          │   Data Sources     │                       │
│                          │  25 RSS/API feeds  │                       │
│                          │  (AI + Finance)    │                       │
│                          └───────────────────┘                       │
│                                                                      │
│                          ┌───────────────────┐                       │
│                          │   AI Providers     │                       │
│                          │  OpenAI + Claude   │                       │
│                          │  (with fallback)   │                       │
│                          └───────────────────┘                       │
└──────────────────────────────────────────────────────────────────────┘
```

### 5.2 Backend Module Architecture

```
pulse-backend/
├── src/main/java/com/pulse/
│   │
│   ├── config/                          # ⚙️ Configuration
│   │   ├── AppConfig.java               #   General app config
│   │   ├── AiProviderConfig.java        #   AI provider settings
│   │   └── CorsConfig.java              #   CORS for frontend
│   │
│   ├── collector/                       # 📡 Data Collection Module
│   │   ├── scheduler/
│   │   │   └── FeedCollectorScheduler.java    # Cron-based scheduler
│   │   ├── parser/
│   │   │   ├── RssFeedParser.java             # Parse RSS/Atom feeds
│   │   │   ├── ApiFeedParser.java             # Parse API responses
│   │   │   └── FeedParserFactory.java         # Factory pattern
│   │   ├── client/
│   │   │   ├── RssClient.java                 # Generic RSS fetcher
│   │   │   ├── HackerNewsClient.java          # HN API integration
│   │   │   └── FinnhubClient.java             # Finnhub API integration
│   │   └── service/
│   │       └── CollectorService.java          # Orchestrates collection
│   │
│   ├── processor/                       # 🔄 Content Processing Module
│   │   ├── service/
│   │   │   ├── DeduplicationService.java      # URL + content hash dedup
│   │   │   ├── ContentCleanerService.java     # Strip HTML, normalize
│   │   │   └── CategoryClassifierService.java # Rule-based classification
│   │   └── model/
│   │       └── ProcessedArticle.java
│   │
│   ├── ai/                              # 🤖 AI Integration Module
│   │   ├── service/
│   │   │   ├── AiSummaryService.java          # Article summarization
│   │   │   ├── AiTaggingService.java          # Auto-tag generation
│   │   │   ├── SentimentAnalysisService.java  # Sentiment scoring
│   │   │   └── AiProviderRouter.java          # OpenAI/Claude routing + fallback
│   │   ├── client/
│   │   │   ├── OpenAiClient.java              # OpenAI REST client
│   │   │   └── ClaudeClient.java              # Anthropic REST client
│   │   ├── prompt/
│   │   │   └── PromptTemplates.java           # Prompt template management
│   │   └── dto/
│   │       ├── AiSummaryRequest.java
│   │       └── AiSummaryResponse.java
│   │
│   ├── article/                         # 📰 Article Domain Module
│   │   ├── entity/
│   │   │   ├── Article.java
│   │   │   ├── Source.java
│   │   │   ├── AiSummary.java
│   │   │   ├── Tag.java
│   │   │   └── ArticleTag.java
│   │   ├── repository/
│   │   │   ├── ArticleRepository.java
│   │   │   ├── SourceRepository.java
│   │   │   └── AiSummaryRepository.java
│   │   ├── service/
│   │   │   ├── ArticleService.java
│   │   │   └── SourceService.java
│   │   ├── resource/
│   │   │   ├── ArticleResource.java           # /api/v1/articles
│   │   │   └── SourceResource.java            # /api/v1/sources
│   │   └── dto/
│   │       ├── ArticleDto.java
│   │       ├── ArticleFilterDto.java
│   │       └── SourceDto.java
│   │
│   ├── knowledge/                       # 📚 Knowledge Base Module
│   │   ├── entity/
│   │   │   ├── SavedArticle.java
│   │   │   ├── Note.java
│   │   │   └── Collection.java
│   │   ├── repository/
│   │   │   ├── SavedArticleRepository.java
│   │   │   └── CollectionRepository.java
│   │   ├── service/
│   │   │   ├── BookmarkService.java
│   │   │   └── SearchService.java
│   │   ├── resource/
│   │   │   ├── BookmarkResource.java          # /api/v1/bookmarks
│   │   │   └── CollectionResource.java        # /api/v1/collections
│   │   └── dto/
│   │       ├── BookmarkDto.java
│   │       └── CollectionDto.java
│   │
│   └── common/                          # 🔧 Shared Utilities
│       ├── exception/
│       │   ├── GlobalExceptionHandler.java
│       │   └── AppException.java
│       ├── dto/
│       │   ├── PagedResponse.java
│       │   └── ErrorResponse.java
│       └── util/
│           ├── SlugUtil.java
│           └── DateUtil.java
│
├── src/main/resources/
│   ├── application.properties           # Main config
│   ├── application-dev.properties       # Dev profile
│   ├── application-prod.properties      # Prod profile
│   ├── db/migration/                    # Flyway
│   │   ├── V1.0.0__create_source_table.sql
│   │   ├── V1.0.1__create_article_table.sql
│   │   ├── V1.0.2__create_ai_summary_table.sql
│   │   ├── V1.0.3__create_tag_tables.sql
│   │   ├── V1.0.4__create_knowledge_tables.sql
│   │   ├── V1.0.5__add_fulltext_search.sql
│   │   └── V1.1.0__seed_default_sources.sql
│   └── prompts/
│       ├── summarize_en.txt
│       ├── summarize_vi.txt
│       └── classify.txt
│
├── pom.xml
├── Dockerfile
├── .env.example
└── README.md
```

### 5.3 Frontend Architecture

```
pulse-frontend/
├── public/
│   └── favicon.svg
│
├── src/
│   ├── api/                             # 🌐 API Client Layer
│   │   ├── client.ts                    #   Axios instance + interceptors
│   │   ├── articles.ts                  #   Article API calls
│   │   ├── sources.ts                   #   Source API calls
│   │   ├── knowledge.ts                 #   Bookmark/Collection API calls
│   │   └── types.ts                     #   API response types
│   │
│   ├── components/                      # 🧩 Reusable Components
│   │   ├── ui/                          #   Base components
│   │   │   ├── Button.tsx
│   │   │   ├── Card.tsx
│   │   │   ├── Modal.tsx
│   │   │   ├── Badge.tsx
│   │   │   ├── Skeleton.tsx
│   │   │   ├── SearchInput.tsx
│   │   │   └── ThemeToggle.tsx          #   Dark/Light mode toggle
│   │   │
│   │   ├── layout/                      #   Layout components
│   │   │   ├── AppLayout.tsx
│   │   │   ├── Header.tsx
│   │   │   ├── Sidebar.tsx
│   │   │   └── Footer.tsx
│   │   │
│   │   ├── article/                     #   Article components
│   │   │   ├── ArticleCard.tsx          #   Card: thumbnail + title + source
│   │   │   ├── ArticleList.tsx          #   Grid/List view (MSN-style)
│   │   │   ├── ArticlePreview.tsx       #   Hover preview (summary + link)
│   │   │   ├── AiSummaryPanel.tsx       #   AI summary display
│   │   │   └── SentimentBadge.tsx       #   Sentiment indicator
│   │   │
│   │   ├── feed/                        #   Feed components
│   │   │   ├── FeedTimeline.tsx
│   │   │   ├── FeedFilter.tsx           #   Domain/source/date filters
│   │   │   ├── DomainSelector.tsx       #   AI / Finance toggle
│   │   │   └── TrendingBar.tsx
│   │   │
│   │   └── knowledge/                   #   Knowledge base components
│   │       ├── BookmarkButton.tsx
│   │       ├── BookmarkList.tsx
│   │       ├── NoteEditor.tsx           #   Markdown note editor
│   │       ├── CollectionCard.tsx
│   │       └── CollectionManager.tsx
│   │
│   ├── pages/                           # 📄 Route Pages
│   │   ├── Dashboard.tsx                #   Main feed dashboard (MSN-style grid)
│   │   ├── KnowledgePage.tsx            #   Saved articles + notes
│   │   ├── SourcesPage.tsx              #   Manage feed sources
│   │   └── SettingsPage.tsx             #   User preferences
│   │   # NOTE: No ArticlePage — click card opens original URL (new tab)
│   │
│   ├── hooks/                           # 🪝 Custom Hooks
│   │   ├── useArticles.ts              #   Article data fetching
│   │   ├── useBookmarks.ts             #   Bookmark operations
│   │   ├── useSearch.ts                #   Search functionality
│   │   ├── useSources.ts              #   Source management
│   │   └── useTheme.ts                #   Dark/Light theme
│   │
│   ├── store/                           # 🗃️ State Management (Zustand)
│   │   ├── articleStore.ts
│   │   ├── filterStore.ts
│   │   └── themeStore.ts
│   │
│   ├── styles/                          # 🎨 Styles
│   │   ├── index.css                    #   Global styles + reset
│   │   ├── variables.css                #   CSS custom properties (light+dark)
│   │   └── animations.css               #   Micro-animations
│   │
│   ├── utils/                           # 🔧 Utilities
│   │   ├── formatDate.ts
│   │   ├── truncateText.ts
│   │   └── constants.ts
│   │
│   ├── App.tsx                          # App root + routing
│   └── main.tsx                         # Entry point
│
├── index.html
├── vite.config.ts
├── tsconfig.json
├── package.json
├── Dockerfile
└── README.md
```

---

## 6. Database Schema (Optimized — Metadata Only)

> **Nguyên tắc thiết kế**: Lưu tối thiểu dữ liệu cần thiết. Không lưu full content bài viết.
> Khi muốn đọc chi tiết → redirect đến `original_url`.

### 6.1 Storage Comparison

| Approach | Per Article | 1000 articles/month | 1 year |
|----------|-------------|---------------------|--------|
| **Full content** | ~5-50 KB | ~25 MB | ~300 MB |
| **Metadata-only** ✅ | ~0.5-1 KB | ~0.75 MB | ~9 MB |
| **Savings** | **90-98%** | **97%** | **97%** |

### 6.2 ER Diagram

```
┌─────────────────┐       ┌─────────────────────────────────────┐
│     SOURCE      │       │            ARTICLE                  │
├─────────────────┤       ├─────────────────────────────────────┤
│ id          PK  │──┐    │ id                    PK            │
│ name            │  │    │ source_id             FK ───────────┘
│ url             │  │    │ title         VARCHAR(500)          │
│ feed_url        │  │    │ description   VARCHAR(500) ← tóm tắt│
│ icon_url        │  └───►│ original_url  VARCHAR(1000) UNIQUE  │
│ type            │       │ author        VARCHAR(255)          │
│ domain          │       │ thumbnail_url VARCHAR(1000)         │
│ language        │       │ domain        VARCHAR(50)           │
│ status          │       │ category      VARCHAR(100)          │
│ last_fetched_at │       │ language      VARCHAR(10)           │
│ fetch_interval  │       │ published_at  TIMESTAMP             │
│ error_count     │       │ search_vector TSVECTOR              │
│ created_at      │       │ created_at    TIMESTAMP             │
│ updated_at      │       └──────────┬──────────────────────────┘
└─────────────────┘                  │
                          ┌──────────┴──────────┐
                          │                     │
                          ▼                     ▼
                 ┌─────────────────┐   ┌─────────────────────┐
                 │  SAVED_ARTICLE  │   │    AI_SUMMARY       │
                 ├─────────────────┤   ├─────────────────────┤
                 │ id          PK  │   │ id              PK  │
                 │ article_id  FK  │   │ article_id      FK  │
                 │ user_note   TEXT│   │ summary    VARCHAR  │
                 │ status          │   │ key_insights  JSON  │
                 │ saved_at        │   │ sentiment           │
                 │ updated_at      │   │ sentiment_score     │
                 └────────┬────────┘   │ tags          JSON  │
                          │            │ ai_provider         │
                          ▼            │ model_used          │
                 ┌─────────────────┐   │ generated_at        │
                 │COLLECTION_ARTICLE│   └─────────────────────┘
                 ├─────────────────┤
                 │ collection_id FK│──► COLLECTION (id, name, description)
                 │ saved_article_id│
                 └─────────────────┘

                 ┌─────────────────┐
                 │  ARTICLE_TAG    │──► TAG (id, name, color)
                 │ article_id  FK  │
                 │ tag_id      FK  │
                 │ tag_source      │
                 └─────────────────┘
```

> **Lưu ý quan trọng:**
> - `description` = preview text từ RSS `<description>` (~100-200 chars), KHÔNG phải full content
> - `original_url` = link đến bài viết gốc, user click → open new tab
> - `thumbnail_url` = ảnh thumbnail từ RSS `<media:content>` hoặc `<enclosure>`
> - Không có cột `content` hay `raw_content` → tiết kiệm 97% storage

### 6.3 SQL Schema (Flyway Migrations)

```sql
-- V1.0.0__create_source_table.sql
CREATE TABLE source (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(255) NOT NULL,
    url             VARCHAR(500) NOT NULL,              -- Source homepage
    feed_url        VARCHAR(500) NOT NULL,              -- RSS/API endpoint
    icon_url        VARCHAR(500),                       -- Source favicon/logo
    type            VARCHAR(20) NOT NULL DEFAULT 'RSS', -- RSS, API
    domain          VARCHAR(50) NOT NULL,               -- AI, FINANCE
    language        VARCHAR(10) NOT NULL DEFAULT 'en',  -- en, vi
    status          VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, INACTIVE, ERROR
    last_fetched_at TIMESTAMP,
    fetch_interval_minutes INT NOT NULL DEFAULT 30,
    error_count     INT NOT NULL DEFAULT 0,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

-- V1.0.1__create_article_table.sql
-- ⚡ OPTIMIZED: Metadata-only, NO full content storage
CREATE TABLE article (
    id              BIGSERIAL PRIMARY KEY,
    source_id       BIGINT NOT NULL REFERENCES source(id),
    title           VARCHAR(500) NOT NULL,
    description     VARCHAR(500),                       -- Short preview from RSS <description>
    original_url    VARCHAR(1000) NOT NULL UNIQUE,      -- Link to original article
    author          VARCHAR(255),
    thumbnail_url   VARCHAR(1000),                      -- Image from RSS <media:content>
    domain          VARCHAR(50) NOT NULL,               -- AI, FINANCE
    category        VARCHAR(100),
    language        VARCHAR(10) NOT NULL DEFAULT 'en',
    published_at    TIMESTAMP,
    search_vector   TSVECTOR,                           -- FTS on title + description
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);
-- NOTE: No 'content' or 'raw_content' columns — user clicks → opens original_url

CREATE INDEX idx_article_domain ON article(domain);
CREATE INDEX idx_article_source_id ON article(source_id);
CREATE INDEX idx_article_published_at ON article(published_at DESC);
CREATE INDEX idx_article_search ON article USING GIN(search_vector);

-- V1.0.2__create_ai_summary_table.sql
-- AI generates summary from description + title (fetches page content on-demand if needed)
CREATE TABLE ai_summary (
    id              BIGSERIAL PRIMARY KEY,
    article_id      BIGINT NOT NULL UNIQUE REFERENCES article(id),
    summary         VARCHAR(1000) NOT NULL,             -- Capped at 1000 chars
    key_insights    JSONB,                              -- ["insight1", "insight2", ...]
    sentiment       VARCHAR(20),                        -- POSITIVE, NEGATIVE, NEUTRAL
    sentiment_score FLOAT,                              -- -1.0 to 1.0
    tags            JSONB,                              -- ["tag1", "tag2", ...]
    ai_provider     VARCHAR(50) NOT NULL,               -- OPENAI, CLAUDE
    model_used      VARCHAR(100) NOT NULL,
    generated_at    TIMESTAMP NOT NULL DEFAULT NOW()
);

-- V1.0.3__create_tag_tables.sql
CREATE TABLE tag (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(100) NOT NULL UNIQUE,
    color           VARCHAR(7) DEFAULT '#6366f1'
);

CREATE TABLE article_tag (
    article_id      BIGINT NOT NULL REFERENCES article(id),
    tag_id          BIGINT NOT NULL REFERENCES tag(id),
    tag_source      VARCHAR(10) NOT NULL DEFAULT 'AI',  -- AI, USER
    PRIMARY KEY (article_id, tag_id)
);

-- V1.0.4__create_knowledge_tables.sql
CREATE TABLE saved_article (
    id              BIGSERIAL PRIMARY KEY,
    article_id      BIGINT NOT NULL UNIQUE REFERENCES article(id),
    user_note       TEXT,
    status          VARCHAR(20) NOT NULL DEFAULT 'UNREAD', -- READ, UNREAD, ARCHIVED
    saved_at        TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE collection (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(255) NOT NULL,
    description     TEXT,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE collection_article (
    collection_id       BIGINT NOT NULL REFERENCES collection(id) ON DELETE CASCADE,
    saved_article_id    BIGINT NOT NULL REFERENCES saved_article(id) ON DELETE CASCADE,
    PRIMARY KEY (collection_id, saved_article_id)
);

-- V1.0.5__add_fulltext_search.sql
-- FTS on title + description only (no content column)
CREATE OR REPLACE FUNCTION update_search_vector()
RETURNS TRIGGER AS $$
BEGIN
    NEW.search_vector :=
        setweight(to_tsvector('english', COALESCE(NEW.title, '')), 'A') ||
        setweight(to_tsvector('english', COALESCE(NEW.description, '')), 'B') ||
        setweight(to_tsvector('english', COALESCE(NEW.author, '')), 'C');
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_article_search_vector
    BEFORE INSERT OR UPDATE OF title, description, author ON article
    FOR EACH ROW EXECUTE FUNCTION update_search_vector();

-- V1.1.0__seed_default_sources.sql
-- Seed AI sources
INSERT INTO source (name, url, feed_url, type, domain, language) VALUES
('OpenAI Blog',       'https://openai.com',          'https://openai.com/blog/rss.xml',                          'RSS', 'AI', 'en'),
('Google AI Blog',    'https://blog.google',         'https://blog.google/technology/ai/rss/',                    'RSS', 'AI', 'en'),
('DeepMind Blog',     'https://deepmind.google',     'https://deepmind.google/blog/rss.xml',                     'RSS', 'AI', 'en'),
('MIT Tech Review',   'https://technologyreview.com','https://www.technologyreview.com/feed/',                    'RSS', 'AI', 'en'),
('Hugging Face Blog', 'https://huggingface.co',      'https://huggingface.co/blog/feed.xml',                     'RSS', 'AI', 'en'),
('arXiv cs.AI',       'https://arxiv.org',           'http://arxiv.org/rss/cs.AI',                               'RSS', 'AI', 'en'),
('VentureBeat AI',    'https://venturebeat.com',     'https://venturebeat.com/category/ai/feed/',                'RSS', 'AI', 'en'),
('MarkTechPost',      'https://marktechpost.com',    'https://www.marktechpost.com/feed/',                       'RSS', 'AI', 'en'),
('The Verge AI',      'https://theverge.com',        'https://www.theverge.com/rss/ai-artificial-intelligence/index.xml','RSS','AI','en');

-- Seed Finance (International) sources
INSERT INTO source (name, url, feed_url, type, domain, language) VALUES
('Reuters Business',  'https://reuters.com',         'https://www.reuters.com/business/rss',                      'RSS', 'FINANCE', 'en'),
('MarketWatch',       'https://marketwatch.com',     'https://feeds.marketwatch.com/marketwatch/topstories/',      'RSS', 'FINANCE', 'en'),
('CNBC',              'https://cnbc.com',            'https://www.cnbc.com/id/100003114/device/rss/rss.html',      'RSS', 'FINANCE', 'en'),
('Seeking Alpha',     'https://seekingalpha.com',    'https://seekingalpha.com/market_currents.xml',               'RSS', 'FINANCE', 'en'),
('Investopedia',      'https://investopedia.com',    'https://www.investopedia.com/feedbuilder/feed/getfeed/?feedName=rss_articles','RSS','FINANCE','en'),
('CoinDesk',          'https://coindesk.com',        'https://www.coindesk.com/arc/outboundfeeds/rss/',            'RSS', 'FINANCE', 'en'),
('Yahoo Finance',     'https://finance.yahoo.com',   'https://finance.yahoo.com/news/rssindex',                    'RSS', 'FINANCE', 'en');

-- Seed Finance (Vietnamese) sources
INSERT INTO source (name, url, feed_url, type, domain, language) VALUES
('CafeF',             'https://cafef.vn',            'https://cafef.vn/rss/trang-chu.rss',                        'RSS', 'FINANCE', 'vi'),
('VnExpress Kinh Doanh','https://vnexpress.net',     'https://vnexpress.net/rss/kinh-doanh.rss',                  'RSS', 'FINANCE', 'vi'),
('VnExpress Tài Chính','https://vnexpress.net',      'https://vnexpress.net/rss/tai-chinh.rss',                   'RSS', 'FINANCE', 'vi'),
('VietStock',         'https://vietstock.vn',        'https://vietstock.vn/rss/tai-chinh.rss',                    'RSS', 'FINANCE', 'vi'),
('Thanh Niên Kinh Tế','https://thanhnien.vn',        'https://thanhnien.vn/rss/kinh-te.rss',                      'RSS', 'FINANCE', 'vi'),
('Tuổi Trẻ Kinh Doanh','https://tuoitre.vn',        'https://tuoitre.vn/rss/kinh-doanh.rss',                     'RSS', 'FINANCE', 'vi');
```

### 6.4 What We Extract From RSS Feeds

Mỗi RSS feed item chứa các field sau — ta chỉ lấy metadata:

```xml
<item>
  <title>GPT-5 Released</title>                    → article.title
  <link>https://openai.com/blog/gpt-5</link>       → article.original_url
  <description>OpenAI announces...</description>   → article.description (truncate 500)
  <author>OpenAI Team</author>                      → article.author
  <pubDate>Mon, 20 Apr 2026</pubDate>              → article.published_at
  <media:content url="https://img..."/>            → article.thumbnail_url
  <category>AI</category>                          → article.category
</item>
```

> **Không fetch/parse full page content.** Chỉ dùng data có sẵn trong RSS feed.

---

## 7. REST API Specification

### 7.1 Articles API

```yaml
# GET /api/v1/articles
# Query Parameters:
#   domain:    AI | FINANCE (comma-separated)
#   category:  research | news | analysis (comma-separated)
#   source_id: 1,2,3
#   language:  en | vi
#   from:      2026-04-01 (ISO date)
#   to:        2026-04-20
#   sentiment: POSITIVE | NEGATIVE | NEUTRAL
#   has_summary: true | false
#   sort:      published_at | created_at (default: published_at)
#   order:     desc | asc (default: desc)
#   page:      0 (default: 0)
#   size:      20 (default: 20, max: 100)
#
# Response: PagedResponse<ArticleDto>

# GET /api/v1/articles/{id}
# Response: ArticleDetailDto (includes AI summary if available)

# GET /api/v1/articles/{id}/summary
# Response: AiSummaryDto

# POST /api/v1/articles/{id}/summary
# Description: Generate AI summary on-demand
# Response: AiSummaryDto (201 Created)

# GET /api/v1/articles/search?q={query}
# Additional params: domain, language, page, size
# Response: PagedResponse<ArticleDto>

# GET /api/v1/articles/trending
# Query: domain, period=24h|7d|30d, limit=10
# Response: List<ArticleDto>
```

### 7.2 Sources API

```yaml
# GET    /api/v1/sources                    → List all sources
# GET    /api/v1/sources/{id}               → Source detail + stats
# POST   /api/v1/sources                    → Add new source
# PUT    /api/v1/sources/{id}               → Update source config
# DELETE /api/v1/sources/{id}               → Remove source
# POST   /api/v1/sources/{id}/fetch         → Trigger immediate fetch
# GET    /api/v1/sources/{id}/articles       → Articles from this source
# GET    /api/v1/sources/stats              → Source health/performance stats
```

### 7.3 Knowledge Base API

```yaml
# POST   /api/v1/bookmarks                  → Bookmark an article
# GET    /api/v1/bookmarks                  → List bookmarks (filterable)
# GET    /api/v1/bookmarks/{id}             → Bookmark detail
# PUT    /api/v1/bookmarks/{id}             → Update note/status
# DELETE /api/v1/bookmarks/{id}             → Remove bookmark

# GET    /api/v1/collections                → List collections
# POST   /api/v1/collections               → Create collection
# PUT    /api/v1/collections/{id}           → Update collection
# DELETE /api/v1/collections/{id}           → Delete collection
# POST   /api/v1/collections/{id}/articles  → Add article to collection
# DELETE /api/v1/collections/{id}/articles/{articleId} → Remove from collection
```

### 7.4 Response DTOs

```java
// PagedResponse<T>
{
    "content": [...],
    "page": 0,
    "size": 20,
    "totalElements": 150,
    "totalPages": 8,
    "hasNext": true
}

// ArticleDto — Metadata only, no full content
{
    "id": 1,
    "title": "GPT-5 Released with Multimodal Capabilities",
    "description": "OpenAI announces GPT-5 with native multimodal...",  // ~200 chars max
    "originalUrl": "https://openai.com/blog/gpt-5",                    // ← Click → open this
    "author": "OpenAI Team",
    "thumbnailUrl": "https://cdn.openai.com/gpt5-hero.jpg",
    "domain": "AI",
    "category": "news",
    "language": "en",
    "source": {
        "id": 1,
        "name": "OpenAI Blog",
        "iconUrl": "https://openai.com/favicon.ico"
    },
    "publishedAt": "2026-04-20T10:00:00Z",
    "timeAgo": "2 hours ago",                                          // Computed field
    "hasSummary": true,
    "isBookmarked": false,
    "tags": ["gpt", "multimodal", "release"]
}

// AiSummaryDto
{
    "id": 1,
    "summary": "OpenAI has released GPT-5 with...",                    // Max 1000 chars
    "keyInsights": [
        "Native multimodal support",
        "2x context window",
        "50% cost reduction"
    ],
    "sentiment": "POSITIVE",
    "sentimentScore": 0.85,
    "tags": ["gpt-5", "multimodal", "ai-release"],
    "aiProvider": "OPENAI",
    "modelUsed": "gpt-4o-mini",
    "generatedAt": "2026-04-20T10:30:00Z"
}
```

---

## 8. Data Flow Diagrams

### 8.1 Article Collection Pipeline

```
┌──────────┐     ┌──────────────┐     ┌─────────────┐
│ Scheduler │────►│  Collector   │────►│   Parser    │
│ (30 min)  │     │  Service     │     │ RSS / API   │
└──────────┘     └──────────────┘     └──────┬──────┘
                                             │
                                    Raw Articles (List)
                                             │
                                             ▼
                                    ┌─────────────────┐
                                    │  Deduplication   │
                                    │  (URL + Hash)    │
                                    └────────┬────────┘
                                             │
                                    New Articles Only
                                             │
                                             ▼
                                    ┌─────────────────┐
                                    │ Metadata Extract │
                                    │ (Title,Desc,Img) │
                                    └────────┬────────┘
                                             │
                                             ▼
                                    ┌─────────────────┐
                                    │   Classifier    │
                                    │ (Domain/Category)│
                                    └────────┬────────┘
                                             │
                                             ▼
                                    ┌─────────────────┐
                                    │ Save to Database │
                                    │   PostgreSQL     │
                                    └────────┬────────┘
                                             │
                              ┌──────────────┴──────────────┐
                              │                             │
                              ▼                             ▼
                     ┌─────────────────┐           ┌────────────┐
                     │  AI Summary     │           │    Done    │
                     │  Queue (Batch)  │           └────────────┘
                     └────────┬────────┘
                              │
                              ▼
                     ┌─────────────────┐
                     │ OpenAI / Claude │
                     │ (with fallback) │
                     └────────┬────────┘
                              │
                              ▼
                     ┌─────────────────┐
                     │ Save AI Summary │
                     └─────────────────┘
```

### 8.2 User Interaction Flow (MSN-Style)

```
Dashboard (Card Grid)
     │
     ├── Browse Cards ──► [Click Card] ──► 🔗 Open Original URL (new tab)
     │                         │
     │                    [Hover/Expand]
     │                         │
     │                    ┌────┴─────┐
     │                    │ Preview: │
     │                    │ Summary  │
     │                    │ AI Tags  │
     │                    │ Actions: │
     │                    │ 📌 Save  │
     │                    │ 📝 Note  │
     │                    │ 📁 Collect│
     │                    │ 🤖 AI Sum │
     │                    └──────────┘
     │
     ├── Filter/Search (Domain, Source, Tags)
     │
     └── Knowledge Base (Saved, Notes, Collections)
```

> **Key UX**: Click card → opens original article in new tab.
> Actions (save/note/AI summary) available via hover preview or action buttons on card.

---

## 9. Key Technical Decisions

### 9.1 Modular Monolith (not Microservices)

| Aspect | Modular Monolith | Microservices |
|--------|-----------------|---------------|
| Complexity | ✅ Low | ❌ High |
| MVP Speed | ✅ Fast | ❌ Slow |
| Deployment | ✅ Single JAR | ❌ Multiple services |
| Communication | ✅ In-process | ❌ Network calls |
| DevOps | ✅ Minimal | ❌ K8s/orchestration |

> **Decision:** Start monolith → extract services if/when needed.

### 9.2 RSS Polling Strategy

| Phase | Strategy | Interval |
|-------|----------|----------|
| **Phase 1 (MVP)** | Fixed interval | Every 30 minutes |
| **Phase 2** | Adaptive | 15-60 min based on source frequency |
| **Phase 3+** | ETag + Adaptive | Bandwidth-efficient + responsive |

### 9.3 AI Provider Routing

```java
// AiProviderRouter.java — Simplified logic
public AiResponse process(Article article) {
    try {
        return openAiClient.summarize(article);        // Try OpenAI first
    } catch (AiProviderException e) {
        log.warn("OpenAI failed, falling back to Claude: {}", e.getMessage());
        try {
            return claudeClient.summarize(article);    // Fallback to Claude
        } catch (AiProviderException e2) {
            log.error("Both AI providers failed for article {}", article.getId());
            retryQueue.add(article.getId());            // Queue for retry
            throw e2;
        }
    }
}
```

### 9.4 AI Cost Management

| Strategy | Detail |
|----------|--------|
| **Batch processing** | Every 1-2 hours (not real-time) |
| **Model tier** | GPT-4o-mini / Claude 3.5 Haiku |
| **Token budget** | Max 2000 input + 500 output per article |
| **Skip rules** | Don't summarize articles < 200 chars |
| **No re-process** | Skip articles that already have summaries |
| **Monthly cap** | Alert at $15, hard stop at $25 |
| **Est. monthly** | ~$5-15 for ~1000 articles |

### 9.5 Dark Mode Strategy

```css
/* variables.css */
:root {
    /* Light theme (default) */
    --bg-primary: #ffffff;
    --bg-secondary: #f8fafc;
    --text-primary: #0f172a;
    --text-secondary: #64748b;
    --accent: #6366f1;         /* Indigo */
    --accent-hover: #4f46e5;
    --border: #e2e8f0;
    --card-bg: #ffffff;
    --shadow: 0 1px 3px rgba(0,0,0,0.1);
}

[data-theme="dark"] {
    --bg-primary: #0f172a;
    --bg-secondary: #1e293b;
    --text-primary: #f1f5f9;
    --text-secondary: #94a3b8;
    --accent: #818cf8;
    --accent-hover: #6366f1;
    --border: #334155;
    --card-bg: #1e293b;
    --shadow: 0 1px 3px rgba(0,0,0,0.4);
}
```

---

## 10. Deployment

### 10.1 Development (Docker Compose)

```yaml
# docker-compose.yml
version: '3.8'

services:
  postgres:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: pulse
      POSTGRES_USER: pulse
      POSTGRES_PASSWORD: pulse_dev
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  backend:
    build: ./pulse-backend
    environment:
      QUARKUS_DATASOURCE_JDBC_URL: jdbc:postgresql://postgres:5432/pulse
      QUARKUS_DATASOURCE_USERNAME: pulse
      QUARKUS_DATASOURCE_PASSWORD: pulse_dev
      OPENAI_API_KEY: ${OPENAI_API_KEY}
      CLAUDE_API_KEY: ${CLAUDE_API_KEY}
    ports:
      - "8080:8080"
    depends_on:
      - postgres

  frontend:
    build: ./pulse-frontend
    environment:
      VITE_API_URL: http://localhost:8080
    ports:
      - "3000:3000"
    depends_on:
      - backend

volumes:
  postgres_data:
```

### 10.2 Environment Variables

```env
# .env.example

# Database
POSTGRES_DB=pulse
POSTGRES_USER=pulse
POSTGRES_PASSWORD=change_me_in_production

# AI Providers
OPENAI_API_KEY=sk-...
CLAUDE_API_KEY=sk-ant-...
AI_MONTHLY_BUDGET_USD=25
AI_PRIMARY_PROVIDER=OPENAI           # OPENAI | CLAUDE

# Backend
QUARKUS_HTTP_PORT=8080
QUARKUS_DATASOURCE_JDBC_URL=jdbc:postgresql://localhost:5432/pulse

# Frontend
VITE_API_URL=http://localhost:8080
```

### 10.3 Production

| Option | Cost | Best For |
|--------|------|----------|
| **VPS (DigitalOcean/Hetzner)** | ~$10-20/month | MVP, single user |
| **Railway / Render** | ~$15-25/month | Easy deployment |
| **Kubernetes** | ~$50+/month | Multi-user, Phase 4+ |

---

## 11. Phased Roadmap (Final)

### Phase 1: MVP — News Dashboard 🎯 (6 weeks)

| Week | Backend | Frontend |
|------|---------|----------|
| **1-2** | Quarkus setup, DB schema, Flyway migrations, Source/Article entities | React+Vite setup, design system, CSS variables (dark+light) |
| **3-4** | RSS Collector (Camel), Scheduler, Dedup, Content Cleaner | Dashboard layout, ArticleCard, ArticleList, Sidebar |
| **5** | Articles REST API (CRUD, filter, paginate), Sources REST API | Feed filters (domain, source, date), Article detail page |
| **6** | Source management API, seed data, Docker Compose | Source management UI, dark mode toggle, polish |

**MVP Deliverables:**
- ✅ 25 pre-configured sources (AI + Finance + VN Finance)
- ✅ Auto-fetch every 30 minutes
- ✅ Dashboard with domain filtering
- ✅ Article detail view
- ✅ Source management
- ✅ Dark mode
- ✅ Docker Compose for local dev

---

### Phase 2: AI Intelligence 🧠 (6 weeks)

- AI module with OpenAI + Claude (dual provider + fallback)
- Summary, key insights, auto-tagging, sentiment analysis
- Batch processing with cost management
- AI Digest daily view
- Prompt engineering & templates

---

### Phase 3: Knowledge Base 📚 (6 weeks)

- Bookmarks, notes (markdown), collections
- Full-text search (PostgreSQL tsvector)
- Reading history
- Export (markdown, PDF)

---

### Phase 4: Advanced 🚀 (Future)

- Notifications (email digest)
- Multi-user + Keycloak auth
- Analytics dashboard
- Elasticsearch, Redis
- PWA / Mobile
- Related articles (pgvector embeddings)
- Slack/Discord webhooks

---

## 12. Non-Functional Requirements

| Requirement | Target |
|-------------|--------|
| API Response Time | < 200ms (p95) |
| Feed Refresh | Every 30 minutes |
| Uptime | 99% (self-hosted) |
| Data Retention | Articles metadata: 1 year, Saved: forever |
| Concurrent Users | 1 (MVP) → multi (Phase 4) |
| Storage | ~50MB/year (metadata-only) |
| AI Processing | Within 2 hours of collection |
| Dark Mode | CSS variables, instant toggle |
| Content Strategy | Zero content storage, redirect to original |

---

## 13. Project Directory Structure (Final)

```
product-plan/
└── new-feeding/
    ├── implementation_plan.md          ← This file
    │
    ├── pulse-backend/                  ← Separate Git repo
    │   ├── src/
    │   ├── pom.xml
    │   ├── Dockerfile
    │   ├── .env.example
    │   └── README.md
    │
    ├── pulse-frontend/                 ← Separate Git repo
    │   ├── src/
    │   ├── package.json
    │   ├── Dockerfile
    │   └── README.md
    │
    └── docker-compose.yml              ← Orchestration
```
