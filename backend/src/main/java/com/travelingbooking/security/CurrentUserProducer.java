package com.travelingbooking.security;

import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Produces;

/**
 * Simple holder for the current authenticated user per request.
 * Populated by FirebaseAuthFilter.
 */
@RequestScoped
public class CurrentUserProducer {

    private CurrentUser currentUser;

    public void set(CurrentUser currentUser) {
        this.currentUser = currentUser;
    }

    @Produces
    @RequestScoped
    public CurrentUser produce() {
        return currentUser;
    }
}

