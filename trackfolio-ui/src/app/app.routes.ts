import { Routes } from '@angular/router';
import { ApplicationsListComponent } from './components/applications-list/applications-list';
import { ApplicationFormComponent } from './components/application-form/application-form'

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'applications' },
  { path: 'applications', component: ApplicationsListComponent },
  { path: 'applications/new', component: ApplicationFormComponent },
  { path: 'applications/:id/edit', component: ApplicationFormComponent },
  { path: '**', redirectTo: 'applications' }
];
