# Pulse Platform — Walkthrough

A fully-implemented MSN-style news aggregation platform with AI intelligence and knowledge management.
Built with **Quarkus** (Java 21) + **React + Vite + TypeScript**.

---

## What Was Built

### Phase 1 — MVP News Dashboard ✅

**Backend (Quarkus)**
- PostgreSQL schema (Flyway migrations V1.0.0 → V1.1.0)
- Article + Source entities with Panache ORM
- RSS feed collector via **Apache Camel** (`CollectorService`)
  - Thumbnail extraction from `media:content`, enclosures, or `<img>` in HTML
  - URL-based deduplication on ingest
  - HTML stripping + description truncation
  - Auto-marks source as `ERROR` after 5+ consecutive failures
- Quarkus Scheduler: every 30 minutes → `FeedCollectorScheduler`
- REST API: `ArticleResource`, `SourceResource`, `CollectorResource`
- Common: `PagedResponse<T>`, `ErrorResponse`, `GlobalExceptionHandler`
- 22 pre-seeded sources: 9 AI (EN) + 7 Finance (EN) + 6 Finance (VI)

**Frontend (React + Vite)**
- Design system with CSS variables (dark/light mode, Inter font)
- `AppLayout` — sticky header, collapsible sidebar with hamburger (mobile)
- `ArticleCard` — thumbnail, title, footer with AI + Bookmark + external link
- `ArticleList` — skeleton shimmer (6 cards), empty state
- `FeedFilter` — All / AI / Finance pill buttons
- `Dashboard` — debounced search (400ms), pagination, manual refresh
- `SourcesPage` — per-source toggle (enable/disable) + fetch-now trigger
- `SettingsPage` — roadmap stub
- Routing: `/`, `/sources`, `/settings`
- Mobile responsive: hamburger sidebar, slide-in overlay, 1-col grid ≤480px

---

### Phase 2 — AI Intelligence ✅

**Backend**
- `AiSummary` entity (JSONB fields: `keyInsights`, `tags`)
- `OpenAiClient` — Java 21 `HttpClient` → `gpt-4o-mini`
- `ClaudeClient` — Anthropic Messages API → `claude-haiku-4-5`
- `AiProviderRouter` — OpenAI-first, Claude fallback, throws only if both fail
- `PromptTemplates` — structured JSON output schema (EN + VI)
- `AiSummaryService` — on-demand + batch (default: 500 articles/month cap)
- `AiSummaryScheduler` — every 2 hours, 5m startup delay
- REST: `GET/POST /api/v1/articles/{id}/summary`, `POST /api/v1/ai/batch`

**Frontend**
- `SentimentBadge` — POSITIVE📈 / NEGATIVE📉 / NEUTRAL⚖️ with score %, dark-mode
- `AiSummaryPanel` — summary + key insights (▸ bullets) + tags + generate button
- `ArticleCard` — Bot🤖 button toggles inline AI panel

---

### Phase 3 — Knowledge Base ✅

**Backend**
- `SavedArticle` entity — note, status (READ/UNREAD/ARCHIVED), `@PreUpdate`
- `Collection` entity — ManyToMany ↔ SavedArticle
- Full CRUD: `BookmarkResource`, `CollectionResource`
- Toggle endpoints: `DELETE /api/v1/bookmarks/by-article/{id}`

**Frontend**
- `BookmarkButton` — amber when saved, spinner during mutation
- `KnowledgePage` — 2-tab layout
  - **Bookmarks**: status filter, thumbnail list, inline note editor, status dropdown, delete
  - **Collections**: create form, card grid, delete
- `/knowledge` route + sidebar nav

---

## Running Locally (Dev Mode)

### Prerequisites
- Java 21+ and Maven 3.9+
- Node.js 20+ and npm
- Docker + Docker Compose (for PostgreSQL)

### 1. Start PostgreSQL
```bash
# From the repo root
docker compose up postgres -d
```

### 2. Start Backend (Quarkus dev mode)
```bash
cd pulse-backend

# Copy env (edit with your API keys)
copy ..\\.env.example .env

mvn quarkus:dev
# Backend: http://localhost:8080
# Swagger UI: http://localhost:8080/q/swagger-ui
```

### 3. Start Frontend (Vite dev server)
```bash
cd pulse-frontend
npm install
npm run dev
# Frontend: http://localhost:5173
```

> **Note**: No VITE_API_URL needed in dev — the default is `http://localhost:8080`.

### Trigger Feed Collection Manually
```bash
curl -X POST http://localhost:8080/api/v1/collector/trigger
```

### Trigger AI Batch Manually
```bash
curl -X POST http://localhost:8080/api/v1/ai/batch
```

---

## Running with Docker Compose (Full Stack)

```bash
# 1. Create .env from example
copy .env.example .env
# Edit .env — add OPENAI_API_KEY and/or CLAUDE_API_KEY if you have them

# 2. Start everything
docker compose up --build -d

# Services:
#  PostgreSQL  → localhost:5432
#  Backend     → http://localhost:8080
#  Frontend    → http://localhost:3000

# 3. Check logs
docker compose logs -f backend

# 4. Stop
docker compose down
```

---

## API Reference

### Articles
| Method | URL | Description |
|--------|-----|-------------|
| `GET` | `/api/v1/articles` | List articles. Params: `domain`, `q`, `language`, `page`, `size` |
| `GET` | `/api/v1/articles/{id}` | Get single article |
| `GET` | `/api/v1/articles/{id}/summary` | Get AI summary (404 if not generated yet) |
| `POST` | `/api/v1/articles/{id}/summary` | Generate AI summary on-demand |

### Sources
| Method | URL | Description |
|--------|-----|-------------|
| `GET` | `/api/v1/sources` | List sources. Param: `domain` |
| `PATCH` | `/api/v1/sources/{id}` | Update status/interval |
| `POST` | `/api/v1/sources/{id}/fetch` | Trigger immediate fetch for this source |

### Bookmarks
| Method | URL | Description |
|--------|-----|-------------|
| `GET` | `/api/v1/bookmarks` | List bookmarks. Param: `status` (READ/UNREAD/ARCHIVED) |
| `POST` | `/api/v1/bookmarks` | Create bookmark (`{ articleId, userNote? }`) |
| `PATCH` | `/api/v1/bookmarks/{id}` | Update note/status |
| `DELETE` | `/api/v1/bookmarks/{id}` | Delete by bookmark id |
| `DELETE` | `/api/v1/bookmarks/by-article/{articleId}` | Unbookmark by article |
| `GET` | `/api/v1/bookmarks/by-article/{articleId}` | Get bookmark for article |

### Collections
| Method | URL | Description |
|--------|-----|-------------|
| `GET` | `/api/v1/collections` | List all collections |
| `POST` | `/api/v1/collections` | Create (`{ name, description? }`) |
| `GET` | `/api/v1/collections/{id}/articles` | List bookmarks in collection |
| `POST` | `/api/v1/collections/{id}/articles/{savedArticleId}` | Add bookmark to collection |
| `DELETE` | `/api/v1/collections/{id}/articles/{savedArticleId}` | Remove from collection |

### Collector / AI
| Method | URL | Description |
|--------|-----|-------------|
| `POST` | `/api/v1/collector/trigger` | Collect all active sources now |
| `POST` | `/api/v1/ai/batch` | Run AI batch summarization now |

---

## Environment Variables

| Variable | Default | Description |
|---|---|---|
| `POSTGRES_PASSWORD` | `pulse_dev` | PostgreSQL password |
| `OPENAI_API_KEY` | _(empty)_ | OpenAI key (gpt-4o-mini) |
| `CLAUDE_API_KEY` | _(empty)_ | Anthropic key (claude-haiku-4-5) |
| `AI_MONTHLY_ARTICLE_CAP` | `500` | Max articles AI-summarised per month (~$1/month) |
| `VITE_API_URL` | `http://localhost:8080` | Backend URL (baked into frontend build) |

> **AI keys are optional.** Without keys the platform still works — article feeds, search, bookmarks, and collections function normally. AI Summary panels show a "Generate" button but calls will fail gracefully.

---

## Project Structure

```
nexus/
├── docker-compose.yml        ← Full stack: postgres + backend + frontend
├── .env.example              ← Copy to .env, add your keys
├── .gitignore
│
├── pulse-backend/            ← Quarkus Java 21
│   ├── src/main/java/com/pulse/
│   │   ├── ai/               ← AI module (clients, service, scheduler, REST)
│   │   ├── article/          ← Core news (entities, services, REST)
│   │   ├── collector/        ← RSS collection (Camel, scheduler)
│   │   ├── common/           ← Shared DTOs, exception handler
│   │   └── knowledge/        ← Bookmarks + collections
│   ├── src/main/resources/
│   │   ├── application.properties
│   │   └── db/migration/     ← Flyway SQL (V1.0.0 → V1.1.0)
│   └── Dockerfile
│
└── pulse-frontend/           ← React + Vite + TypeScript
    ├── src/
    │   ├── api/              ← axios client, typed wrappers
    │   ├── components/       ← ArticleCard, ArticleList, AiSummaryPanel, BookmarkButton, etc.
    │   ├── hooks/            ← useArticles, useSources, useAiSummary, useKnowledge, useDebounce
    │   ├── pages/            ← Dashboard, SourcesPage, KnowledgePage, SettingsPage
    │   ├── store/            ← filterStore, themeStore (Zustand)
    │   └── index.css         ← Design system (CSS variables, all component styles)
    ├── nginx.conf            ← SPA routing config for production
    └── Dockerfile
```

---

## Known Caveats

> [!NOTE]
> The `search_vector` column in `V1.0.5` adds a PostgreSQL trigger, but the `article` table in `V1.0.1` doesn't include the column itself. If you need full-text search via `tsvector`, add `ALTER TABLE article ADD COLUMN search_vector tsvector;` before migration V1.0.5, or rely on the current `ILIKE` fallback in `ArticleService`.

> [!NOTE]
> AI monthly cap is tracked in-memory (resets on restart). For persistent tracking, add a `ai_usage_log` table in a future migration.

> [!TIP]
> To add more RSS sources without code changes, insert directly into the `source` table — the scheduler will pick them up automatically on the next 30-minute cycle, or trigger immediately via `POST /api/v1/collector/trigger`.
