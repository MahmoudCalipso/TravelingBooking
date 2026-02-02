package com.travelingbooking.security;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.io.InputStream;

@ApplicationScoped
@Startup
public class FirebaseAdminProvider {

    private final FirebaseAuth firebaseAuth;

    @Inject
    public FirebaseAdminProvider(
            @ConfigProperty(name = "firebase.project-id") String projectId,
            @ConfigProperty(name = "firebase.service-account.path") String serviceAccountPath
    ) {
        try {
            InputStream serviceAccountStream = Thread.currentThread()
                    .getContextClassLoader()
                    .getResourceAsStream(serviceAccountPath);
            if (serviceAccountStream == null) {
                throw new IllegalStateException("Firebase service account file not found at " + serviceAccountPath);
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setProjectId(projectId)
                    .setCredentials(GoogleCredentials.fromStream(serviceAccountStream))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
            this.firebaseAuth = FirebaseAuth.getInstance();
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize Firebase Admin SDK", e);
        }
    }

    public FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }
}

