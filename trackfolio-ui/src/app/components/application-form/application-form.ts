// src/app/components/application-form/application-form.ts
import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { ApplicationService, Application } from '../../services/application';

@Component({
  selector: 'app-application-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './application-form.html'
})
export class ApplicationFormComponent {
  private fb = inject(FormBuilder);
  private svc = inject(ApplicationService);
  private router = inject(Router);

  saving = false;
  error?: string;

  sources = ['LINKEDIN', 'COMPANY', 'INDEED', 'REFERRAL', 'OTHER'];
  statuses = ['APPLIED', 'INTERVIEW', 'OFFER', 'REJECTED', 'WITHDRAWN'];

  form = this.fb.group({
    company:    ['', [Validators.required, Validators.maxLength(160)]],
    roleTitle:  ['', [Validators.required, Validators.maxLength(160)]],
    source:     ['LINKEDIN', [Validators.required]],
    status:     ['APPLIED',  [Validators.required]],
    appliedAt:  [new Date().toISOString().slice(0,10), [Validators.required]], // yyyy-MM-dd
    location:   [''],
    salaryText: [''],
    jobLink:    [''],
    notes:      [''],
  });

  submit() {
    this.error = undefined;
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const payload = this.form.value as unknown as Application;

    this.saving = true;
    this.svc.create(payload).subscribe({
      next: () => {
        this.saving = false;
        this.router.navigateByUrl('/applications');
      },
      error: (err) => {
        this.saving = false;
        this.error = err?.error?.message ?? err.message ?? 'Failed to save';
      }
    });
  }
}
