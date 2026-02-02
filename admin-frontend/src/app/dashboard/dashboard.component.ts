import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';

@Component({
  standalone: true,
  selector: 'app-dashboard',
  imports: [CommonModule],
  template: `
    <section class="dashboard">
      <h2>Overview</h2>
      <div class="cards">
        <div class="card">
          <h3>Pending Accommodations</h3>
          <p>{{ metrics.pendingAccommodations }}</p>
        </div>
        <div class="card">
          <h3>Pending Events</h3>
          <p>{{ metrics.pendingEvents }}</p>
        </div>
        <div class="card">
          <h3>Pending Programs</h3>
          <p>{{ metrics.pendingPrograms }}</p>
        </div>
      </div>
    </section>
  `
})
export class DashboardComponent implements OnInit {
  metrics = {
    pendingAccommodations: 0,
    pendingEvents: 0,
    pendingPrograms: 0
  };

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.http.get<any>('/api/accommodations/admin/pending').subscribe(list => {
      this.metrics.pendingAccommodations = (list as any[]).length;
    });
    this.http.get<any>('/api/events/admin/pending').subscribe(list => {
      this.metrics.pendingEvents = (list as any[]).length;
    });
    this.http.get<any>('/api/programs/admin/pending').subscribe(list => {
      this.metrics.pendingPrograms = (list as any[]).length;
    });
  }
}

