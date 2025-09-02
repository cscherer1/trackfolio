import { Component, OnInit, inject, PLATFORM_ID } from '@angular/core';
import { CommonModule, DatePipe, isPlatformBrowser } from '@angular/common';
import { ApplicationService, Application } from '../../services/application';

@Component({
  selector: 'app-applications-list',
  standalone: true,
  imports: [CommonModule, DatePipe],
  templateUrl: './applications-list.html'
})
export class ApplicationsListComponent implements OnInit {
  apps: Application[] = [];
  loading = true;
  error?: string;

  private platformId = inject(PLATFORM_ID);

  constructor(private svc: ApplicationService) {}

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      this.load();
    } else {
      // Render a shell during SSR; client will hydrate & load().
      this.loading = false;
    }
  }

  load(): void {
    this.loading = true;
    this.error = undefined;
    this.svc.list().subscribe({
      next: (data) => { this.apps = data; this.loading = false; },
      error: (err) => { this.error = err.message ?? 'Failed to load applications'; this.loading = false; }
    });
  }
}
