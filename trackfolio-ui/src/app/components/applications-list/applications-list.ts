import { Component, OnInit, inject, PLATFORM_ID } from '@angular/core';
import { CommonModule, DatePipe, isPlatformBrowser } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ApplicationService, Application } from '../../services/application';

type SortDir = 'asc' | 'desc';

@Component({
  selector: 'app-applications-list',
  standalone: true,
  imports: [CommonModule, DatePipe, RouterModule],
  templateUrl: './applications-list.html'
})
export class ApplicationsListComponent implements OnInit {
  apps: Application[] = [];
  view: Application[] = [];

  loading = true;
  error?: string;
  deletingId?: string;

  // sorting
  sortKey: keyof Application | 'appliedAt' = 'appliedAt';
  sortDir: SortDir = 'desc';

  // filters (all are optional)
  filters: {
    q?: string;
    status?: string;
    source?: string;
    from?: string; // yyyy-MM-dd
    to?: string;   // yyyy-MM-dd
  } = {};

  private platformId = inject(PLATFORM_ID);

  constructor(private svc: ApplicationService) {}

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      this.load();
    } else {
      this.loading = false; // SSR shell
    }
  }

  /** Pull fresh data from API */
  load(): void {
    this.loading = true;
    this.error = undefined;
    this.svc.list().subscribe({
      next: (data) => {
        // ensure a sane array
        this.apps = Array.isArray(data) ? data : [];
        this.applyView();
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.message ?? 'Failed to load applications';
        this.loading = false;
      }
    });
  }

  /** Filter + sort into this.view */
  private applyView(): void {
    const q = (this.filters.q ?? '').trim().toLowerCase();
    const status = this.filters.status ?? undefined;
    const source = this.filters.source ?? undefined;
    const from = this.parseDateOrUndefined(this.filters.from);
    const to = this.parseDateOrUndefined(this.filters.to);

    // filter
    let rows = this.apps.filter((a) => {
      // text query across several fields
      const matchesQ =
        !q ||
        [a.company, a.roleTitle, a.location, a.notes, a.status, a.source]
          .map((x) => (x ?? '').toString().toLowerCase())
          .some((s) => s.includes(q));

      const matchesStatus = !status || (a.status ?? '') === status;
      const matchesSource = !source || (a.source ?? '') === source;

      // date checks (treat missing appliedAt as "no match" when filters present)
      const ad = this.parseDateOrUndefined(a.appliedAt);
      const matchesFrom = !from || (!!ad && ad >= from);
      const matchesTo = !to || (!!ad && ad <= to);

      return matchesQ && matchesStatus && matchesSource && matchesFrom && matchesTo;
    });

    // sort
    const dir = this.sortDir === 'asc' ? 1 : -1;
    const key = this.sortKey;

    rows.sort((a, b) => {
      // dates: special-case appliedAt
      if (key === 'appliedAt') {
        const da = this.parseDateOrUndefined(a.appliedAt);
        const db = this.parseDateOrUndefined(b.appliedAt);
        const na = da ? da.getTime() : Number.NEGATIVE_INFINITY;
        const nb = db ? db.getTime() : Number.NEGATIVE_INFINITY;
        return (na - nb) * dir;
      }

      // strings (fallback to empty string)
      const av = (a[key] ?? '').toString().toLowerCase();
      const bv = (b[key] ?? '').toString().toLowerCase();
      return av.localeCompare(bv) * dir;
    });

    this.view = rows;
  }

  /** Change sort key (toggle direction if same key) */
  setSort(key: keyof Application | 'appliedAt'): void {
    if (this.sortKey === key) {
      this.sortDir = this.sortDir === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortKey = key;
      // sensible default: dates desc, text asc
      this.sortDir = key === 'appliedAt' ? 'desc' : 'asc';
    }
    this.applyView();
  }

  /** Template-friendly query handler */
  onQueryChange(q: string | null | undefined): void {
    this.setFilter('q', q ?? '');
  }

  /** Set a single filter key from template (all values come as strings) */
  setFilter<K extends keyof ApplicationsListComponent['filters']>(
    key: K,
    raw: string | null | undefined
  ): void {
    // normalize: empty string → undefined
    const trimmed = (raw ?? '').toString().trim();
    const val = trimmed === '' ? undefined : trimmed;

    this.filters = { ...this.filters, [key]: val };
    this.applyView();
  }

  /** Clear all filters */
  clearFilters(): void {
    this.filters = {};
    this.applyView();
  }

  /** Delete one row (then refresh the view) */
  remove(id: string): void {
    if (!id) return;
    if (!confirm('Delete this application?')) return;

    this.deletingId = id;
    this.svc.delete(id).subscribe({
      next: () => {
        // drop from local list for instant UI feedback
        this.apps = this.apps.filter((a) => a.id !== id);
        this.applyView();
        this.deletingId = undefined;
      },
      error: (err) => {
        alert(err?.error?.message ?? err.message ?? 'Delete failed');
        this.deletingId = undefined;
      }
    });
  }

  /** Accepts ISO date string or Date and returns Date|undefined */
  private parseDateOrUndefined(value: unknown): Date | undefined {
    if (!value) return undefined;
    if (value instanceof Date && !isNaN(value.getTime())) return value;

    // handles 'yyyy-MM-dd' or full ISO
    const d = new Date(value as string);
    return isNaN(d.getTime()) ? undefined : d;
  }
}
