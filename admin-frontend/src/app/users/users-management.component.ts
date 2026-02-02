import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';

@Component({
  standalone: true,
  selector: 'app-users-management',
  imports: [CommonModule],
  template: `
    <section class="users">
      <h2>Users</h2>
      <div *ngFor="let u of users" class="item">
        <strong>{{ u.email }}</strong> – {{ u.role }} – {{ u.status }}
        <button *ngIf="u.status !== 'ACTIVE'" (click)="setActive(u.id, true)">Activate</button>
        <button *ngIf="u.status === 'ACTIVE'" (click)="setActive(u.id, false)">Block</button>
      </div>
    </section>
  `
})
export class UsersManagementComponent implements OnInit {
  users: any[] = [];

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.refresh();
  }

  refresh() {
    this.http.get<any[]>('/api/admin/users').subscribe(d => this.users = d);
  }

  setActive(userId: string, active: boolean) {
    this.http.put('/api/users/admin/activate', { userId, active }).subscribe(() => this.refresh());
  }
}

