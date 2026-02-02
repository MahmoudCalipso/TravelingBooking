import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';

@Component({
  standalone: true,
  selector: 'app-premium-visibility',
  imports: [CommonModule],
  template: `
    <section class="premium">
      <h2>Premium Visibility</h2>
      <p>Select approved accommodations to feature on the home page.</p>

      <div *ngFor="let a of accommodations" class="item">
        <strong>{{ a.title }}</strong>
        <span *ngIf="a.premiumUntil">Premium until: {{ a.premiumUntil | date }}</span>
        <button (click)="setPremium(a.id, 30)">Set 30 days premium</button>
      </div>
    </section>
  `
})
export class PremiumVisibilityComponent implements OnInit {
  accommodations: any[] = [];

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.refresh();
  }

  refresh() {
    this.http.get<any[]>('/api/accommodations/admin/pending')
      .subscribe(() => {
        // In a real app we'd fetch all approved; simplified example:
        this.http.get<any[]>('/api/accommodations/public/search')
          .subscribe(d => this.accommodations = d);
      });
  }

  setPremium(id: string, days: number) {
    const premiumUntil = new Date();
    premiumUntil.setDate(premiumUntil.getDate() + days);
    this.http.put(`/api/accommodations/admin/${id}/premium`, { premiumUntil })
      .subscribe(() => this.refresh());
  }
}

