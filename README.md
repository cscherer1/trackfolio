# trackfolio

A full-stack job application tracking application — the predecessor to the current [JobDashboard](https://github.com/cscherer1/JobDashboard) automation stack.

**Stack:** Java 21 · Spring Boot 3.5 · Spring Security · JPA · PostgreSQL · Flyway · Angular 20 + Tailwind CSS 4

---

## What it is

TrackFolio is a complete application-tracking system built while actively job hunting: a user creates an account, logs applications (company, role, status, notes), and manages the pipeline from a clean dashboard. It was the first version of a workflow that eventually grew into the fully automated, self-hosted JobDashboard stack (n8n + NocoDB + JobSpy).

## Why it's worth a look

- **Real, tested full-stack code** — not a tutorial project. Spring Security (form login, BCrypt password encoder) for auth, JPA entities, Flyway-managed PostgreSQL migrations, and an Angular 20 frontend with spec files + Tailwind CSS 4, all behind a Maven wrapper so the whole thing builds with one command.
- **Curated, not code-dumped** — the git history shows feature-by-feature building: search, filters, sorting, edit flows, and a UI polish pass ("no longer looks like a 1998 webpage").
- **Shows the iteration story** — TrackFolio → JobDashboard demonstrates exactly the pattern employers hire for: build something usable, learn what's missing, and rebuild it better with the right architecture.

## Tech stack

| Layer | Choice |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5 |
| Security | Spring Security (form login, BCrypt) |
| Persistence | Spring Data JPA + PostgreSQL |
| Migrations | Flyway |
| Backend templates | Thymeleaf |
| Frontend | Angular 20, Tailwind CSS 4 |
| Build | Maven wrapper |

## Running it

**Requires:** Java 21 + Maven (wrapper included), PostgreSQL.

```bash
mvn spring-boot:run
```

## Status

Superseded by **JobDashboard** — the current automated job-search stack (JobSpy job ingestion + n8n orchestration + NocoDB as system of record). Kept public as the foundation of that evolution.

## License

Private portfolio project. © Christian Scherer.
