## End-to-End Travel & Discovery Platform

This repository contains a complete, production-ready multi-app platform for travel discovery and booking, built around:

- **Backend**: Quarkus (Java, REST, role-based access, admin workflows)
- **Admin & Supplier Web**: Angular 21
- **Mobile App**: Flutter (Android & iOS)
- **Cloud Backend Services**: Firebase Authentication, Firestore / Realtime Database, Firebase Storage

The platform supports:

- **Super Admin** (system owner with private admin access)
- **Traveler**
- **Supplier Subscriber (Fournisseur)**

All content (accommodations, explore stays, events, travel programs, premium placements) is **subject to Super Admin approval** and **role-based access control**.

---

### 1. Repository Structure

- `backend/` – Quarkus project (REST APIs, RBAC, approvals, persistence)
- `admin-frontend/` – Angular 21 app for Super Admin & Supplier web portal
- `mobile-app/` – Flutter app for Travelers and Suppliers on Android/iOS

Each sub-project has its own dedicated `README` for setup and development details.

---

### 2. High-Level Architecture

- **Authentication**
  - Firebase Authentication (Google + email/password)
  - Flutter and Angular clients obtain Firebase ID tokens after login.
  - Quarkus backend verifies Firebase ID tokens using Firebase Admin SDK and enforces roles.
  - Super Admin is stored and locked at backend level (cannot be created via public flows).

- **Authorization & Roles**
  - Roles: `SUPER_ADMIN`, `TRAVELER`, `SUPPLIER`.
  - Role is persisted in backend database and enforced on every protected API via custom security annotations.
  - Supplier activation and visibility plans are controlled by Super Admin.

- **Data & Storage**
  - Core business data (users, listings, events, travel programs, approvals, payments metadata) lives in a relational DB (e.g. PostgreSQL) via Quarkus + JPA.
  - Media (images) stored in **Firebase Storage**; URLs persisted in backend.
  - Real-time chat stored in **Firestore / Realtime Database** and consumed directly by Flutter (and optionally Angular for moderation).

- **Chat**
  - Each group trip / event has a **chat room** in Firestore / Realtime Database.
  - Access to rooms enforced via Firestore security rules using Firebase Authentication.
  - Admin moderation: Super Admin tools in Angular call backend and Firebase Admin SDK to mute/kick users or lock rooms.

---

### 3. Local Development – Summary

1. **Clone repo** and install prerequisites:
   - Java 21+, Maven
   - Node.js 20+, Angular CLI 17+ (compatible with Angular 21)
   - Flutter SDK (latest stable)
   - Firebase CLI

2. **Configure Firebase project**:
   - Create a Firebase project and enable:
     - Authentication (Google & Email/Password)
     - Firestore or Realtime Database
     - Storage
   - Download:
     - `google-services.json` (Android)
     - `GoogleService-Info.plist` (iOS)
     - Web config (for Angular)
   - Add configuration files under:
     - `mobile-app/android/app/` and `mobile-app/ios/Runner/`
     - `admin-frontend/src/environments/`
     - `backend/src/main/resources/firebase/` (service account for Admin SDK).

3. **Start backend**:
   - See `backend/README.md` for database configuration and Quarkus commands.

4. **Start admin frontend**:
   - See `admin-frontend/README.md` for Angular dev server commands.

5. **Start mobile app**:
   - See `mobile-app/README.md` for Flutter commands (Android/iOS).

---

### 4. Production Readiness Notes

The codebase is structured for production use:

- Explicit **role-based access control** and annotated endpoints.
- **Approval workflows** for all user-generated content.
- Configurable **premium visibility / paid placement** flows.
- Clean separation of concerns between:
  - Public mobile app (Flutter)
  - Admin + Supplier web app (Angular)
  - Core business backend (Quarkus)
  - Real-time messaging (Firebase).

Refer to each subproject README and source code for implementation details of specific features and workflows.

