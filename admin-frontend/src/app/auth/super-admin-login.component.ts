import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { getAuth, signInWithEmailAndPassword } from '@angular/fire/auth';

@Component({
  standalone: true,
  selector: 'app-super-admin-login',
  imports: [CommonModule, FormsModule],
  template: `
    <div class="auth-container">
      <h2>Super Admin Login</h2>
      <form (ngSubmit)="login()">
        <label>Email</label>
        <input [(ngModel)]="email" name="email" type="email" required />

        <label>Password</label>
        <input [(ngModel)]="password" name="password" type="password" required />

        <button type="submit" [disabled]="loading">Login</button>
        <p class="error" *ngIf="error">{{ error }}</p>
      </form>
    </div>
  `
})
export class SuperAdminLoginComponent {
  email = '';
  password = '';
  loading = false;
  error: string | null = null;

  constructor(private router: Router) {}

  async login() {
    this.loading = true;
    this.error = null;
    try {
      const auth = getAuth();
      const cred = await signInWithEmailAndPassword(auth, this.email, this.password);
      const token = await cred.user.getIdToken();
      localStorage.setItem('fb_id_token', token);
      // Backend enforces SUPER_ADMIN; UI only redirects.
      await this.router.navigateByUrl('/');
    } catch (e: any) {
      this.error = e.message || 'Login failed';
    } finally {
      this.loading = false;
    }
  }
}

