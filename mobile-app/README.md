## Traveling Booking Mobile App

Flutter app for **travelers** and **supplier subscribers**, providing:

- Firebase Authentication (email/password, extendable to Google sign-in).
- Role selection during registration (Traveler / Supplier) with backend registration call.
- Browsing of **accommodations**, **events**, and **travel programs** from the Quarkus backend.
- Real-time **group chat rooms** per event/program using Firestore collections.
- Basic profile view and logout.

### Run Locally

1. Configure Firebase with `flutterfire configure` to generate `lib/firebase_options.dart`.
2. Ensure the backend is running on `http://localhost:8080` or adjust the URLs in the screens.
3. Run:

```bash
cd mobile-app
flutter pub get
flutter run
```

