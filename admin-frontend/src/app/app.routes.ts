import { Routes } from '@angular/router';
import { SuperAdminLoginComponent } from './auth/super-admin-login.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { PendingContentComponent } from './content/pending-content.component';
import { UsersManagementComponent } from './users/users-management.component';
import { PremiumVisibilityComponent } from './premium/premium-visibility.component';
import { superAdminGuard } from './auth/super-admin.guard';

export const routes: Routes = [
  {
    path: 'super-admin-login',
    component: SuperAdminLoginComponent
  },
  {
    path: '',
    canActivate: [superAdminGuard],
    children: [
      { path: '', component: DashboardComponent },
      { path: 'pending', component: PendingContentComponent },
      { path: 'users', component: UsersManagementComponent },
      { path: 'premium', component: PremiumVisibilityComponent }
    ]
  },
  { path: '**', redirectTo: '' }
];

