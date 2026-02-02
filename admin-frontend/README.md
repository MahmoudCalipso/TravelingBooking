## Traveling Booking Admin Frontend

Angular-based admin console for **Super Admin** and backoffice operations.

### Features

- Private **Super Admin login** via dedicated route `/super-admin-login` (email/password via Firebase Auth).
- Dashboard with counts of **pending accommodations, events, and travel programs**.
- Approval views to **approve / reject / delete** submitted content.
- User management screen to **activate / block** travelers and suppliers.
- Premium visibility screen to flag approved accommodations for **homepage premium placement**.

### Run Locally

```bash
cd admin-frontend
npm install
npm start
```

Update `src/environments/environment.ts` with your Firebase web configuration.

