## Traveling Booking Backend (Quarkus)

Quarkus-based backend providing REST APIs, persistence, approval workflows, and role-based access control for the travel & discovery platform.

---

### 1. Tech Stack

- **Framework**: Quarkus 3.x
- **Language**: Java 21
- **Database**: PostgreSQL (recommended for production)
- **Auth Integration**: Firebase Authentication (ID tokens verified via Firebase Admin SDK)
- **Build Tool**: Maven

---

### 2. Running Locally

1. **Configure Database**

   Create a PostgreSQL database (e.g. `traveling_booking`) and user:

   ```sql
   CREATE DATABASE traveling_booking;
   CREATE USER traveling_booking_user WITH ENCRYPTED PASSWORD 'change-me';
   GRANT ALL PRIVILEGES ON DATABASE traveling_booking TO traveling_booking_user;
   ```

2. **Configure Firebase Admin**

   - In the Firebase console, create a **service account key** for the project.
   - Download the JSON key and place it at:
     - `src/main/resources/firebase/service-account.json`
   - This key is used **only on the backend** to:
     - Verify Firebase ID tokens from Flutter / Angular clients.
     - Optionally moderate chat rooms via Firestore / Realtime Database.

3. **Set Application Properties**

   Update `src/main/resources/application.properties` with your DB credentials and Firebase project ID.

4. **Run in Dev Mode**

   ```bash
   mvn quarkus:dev
   ```

   The API will be available at `http://localhost:8080`.

---

### 3. Security & Auth Overview

- Clients (Flutter mobile and Angular admin) authenticate with **Firebase Authentication**.
- After login, clients send the **Firebase ID token** in the `Authorization: Bearer <token>` header to the backend.
- A custom **JAX-RS request filter** verifies this token against Firebase Admin SDK, resolves the user in the local database, and enforces roles:
  - `SUPER_ADMIN`
  - `TRAVELER`
  - `SUPPLIER`
- A default **Super Admin user** is created at initialization (via `import.sql` or migration) and cannot be created via public registration.

---

### 4. Main Modules (Backend)

- `user` – User accounts, roles, traveler/supplier profiles, Super Admin lock.
- `accommodation` – Accommodation & explore stays (creation, approval, premium visibility).
- `event` – Events with approval workflow.
- `travelprogram` – Travel programs and group trips.
- `admin` – Global configuration, moderation hooks, premium visibility management.

Each module exposes REST endpoints under `/api/*` with strict role checks.

