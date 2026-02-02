package com.travelingbooking.security;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.travelingbooking.domain.Role;
import com.travelingbooking.domain.user.UserAccount;
import com.travelingbooking.user.UserAccountRepository;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class FirebaseAuthFilter implements ContainerRequestFilter {

    @Inject
    FirebaseAdminProvider firebaseAdminProvider;

    @Inject
    UserAccountRepository userAccountRepository;

    @Inject
    CurrentUserProducer currentUserProducer;

    private static final String API_PREFIX = "/api/";

    @Override
    @Transactional
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String path = requestContext.getUriInfo().getPath();

        // Allow non-API endpoints (health, open docs) without auth
        if (!path.startsWith(API_PREFIX)) {
            return;
        }

        // Allow some specific public endpoints (e.g. health, maybe public listings) here if desired
        if ("GET".equals(requestContext.getMethod()) && path.startsWith("api/public/")) {
            return;
        }

        String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            abortUnauthorized(requestContext, "Missing Authorization header");
            return;
        }

        String token = authHeader.substring("Bearer ".length()).trim();
        try {
            FirebaseToken decodedToken = firebaseAdminProvider.getFirebaseAuth().verifyIdToken(token);
            String firebaseUid = decodedToken.getUid();
            String email = decodedToken.getEmail();
            if (email == null) {
                abortUnauthorized(requestContext, "Firebase user must have an email");
                return;
            }

            Optional<UserAccount> userOpt = userAccountRepository.findByFirebaseUid(firebaseUid);
            if (userOpt.isEmpty()) {
                // For security, we do not auto-create users here; clients must call registration endpoint first.
                abortUnauthorized(requestContext, "User not registered in backend");
                return;
            }

            UserAccount user = userOpt.get();
            if (!"ACTIVE".equalsIgnoreCase(user.status) && !user.isSuperAdmin()) {
                abortUnauthorized(requestContext, "User not active");
                return;
            }

            user.updatedAt = Instant.now();

            CurrentUser currentUser = new CurrentUser(
                    user.id,
                    user.firebaseUid,
                    user.email,
                    user.role == null ? Role.TRAVELER : user.role
            );
            currentUserProducer.set(currentUser);

        } catch (FirebaseAuthException e) {
            abortUnauthorized(requestContext, "Invalid Firebase token: " + e.getMessage());
        }
    }

    private void abortUnauthorized(ContainerRequestContext ctx, String message) {
        ctx.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                .entity(message)
                .build());
    }
}

