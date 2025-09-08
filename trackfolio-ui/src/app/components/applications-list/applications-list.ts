// src/app/components/applications-list/applications-list.ts
import { Component, OnInit, inject, PLATFORM_ID } from '@angular/core';
import { CommonModule, DatePipe, isPlatformBrowser } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ApplicationService, Application } from '../../services/application';

type SortKey = 'company' | 'roleTitle' | 'status' | 'source' | 'appliedAt' | 'location';
type SortDir = 'asc' | 'desc';

@Component({
  selector: 'app-applications-list',
  standalone: true,
  imports: [CommonModule, DatePipe, RouterModule],
  templateUrl: './applications-list.html'
})
export class ApplicationsListComponent implements OnInit {
  apps: Application[] = [];
  view: Application[] = []; // <- the table renders this (sorted copy)
  loading = true;
  error?: string;
  deletingId?: string;

  private platformId = inject(PLATFORM_ID);

  // sorting state
  sortKey: SortKey = 'appliedAt';
  sortDir: SortDir = 'desc';

  constructor(private svc: ApplicationService) {}

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      this.load();
    } else {
      this.loading = false;
    }
  }

  load(): void {
    this.loading = true;
    this.error = undefined;
    this.svc.list().subscribe({
      next: (data) => {
        this.apps = data ?? [];
        this.applyView();            // build sorted view
        this.loading = false;
      },
      error: (err) => {
        this.error = err.message ?? 'Failed to load applications';
        this.loading = false;
      }
    });
  }

  setSort(key: SortKey) {
    if (this.sortKey === key) {
      this.sortDir = this.sortDir === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortKey = key;
      this.sortDir = 'asc';
    }
    this.applyView();
  }

  private applyView() {
    const dir = this.sortDir === 'asc' ? 1 : -1;
    this.view = [...this.apps].sort((a, b) => {
      const ka = (a[this.sortKey] ?? '') as any;
      const kb = (b[this.sortKey] ?? '') as any;
      if (this.sortKey === 'appliedAt') {
        return String(ka).localeCompare(String(kb)) * dir; // ISO date strings sort fine
      }
      return String(ka).localeCompare(String(kb)) * dir;
    });
  }

  remove(id: string) {
    if (!confirm('Delete this application?')) return;
    this.deletingId = id;
    this.svc.delete(id).subscribe({
      next: () => { this.deletingId = undefined; this.load(); },
      error: (err) => { alert(err?.error?.message ?? err.message ?? 'Delete failed'); this.deletingId = undefined; }
    });
  }
}
