import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';

@Component({
  standalone: true,
  selector: 'app-pending-content',
  imports: [CommonModule],
  template: `
    <section class="pending">
      <h2>Pending Approvals</h2>

      <h3>Accommodations</h3>
      <div *ngFor="let a of accommodations" class="item">
        <strong>{{ a.title }}</strong> – {{ a.city }}, {{ a.country }}
        <button (click)="setApproval('accommodations', a.id, true)">Approve</button>
        <button (click)="setApproval('accommodations', a.id, false)">Reject</button>
      </div>

      <h3>Events</h3>
      <div *ngFor="let e of events" class="item">
        <strong>{{ e.title }}</strong> – {{ e.locationName }}
        <button (click)="setApproval('events', e.id, true)">Approve</button>
        <button (click)="setApproval('events', e.id, false)">Reject</button>
      </div>

      <h3>Travel Programs</h3>
      <div *ngFor="let p of programs" class="item">
        <strong>{{ p.title }}</strong> – {{ p.mainDestination }}
        <button (click)="setApproval('programs', p.id, true)">Approve</button>
        <button (click)="setApproval('programs', p.id, false)">Reject</button>
      </div>
    </section>
  `
})
export class PendingContentComponent implements OnInit {
  accommodations: any[] = [];
  events: any[] = [];
  programs: any[] = [];

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.refresh();
  }

  refresh() {
    this.http.get<any[]>('/api/accommodations/admin/pending').subscribe(d => this.accommodations = d);
    this.http.get<any[]>('/api/events/admin/pending').subscribe(d => this.events = d);
    this.http.get<any[]>('/api/programs/admin/pending').subscribe(d => this.programs = d);
  }

  setApproval(type: 'accommodations' | 'events' | 'programs', id: string, approved: boolean) {
    this.http.put(`/api/${type}/admin/${id}/approval`, { approved }).subscribe(() => this.refresh());
  }
}

