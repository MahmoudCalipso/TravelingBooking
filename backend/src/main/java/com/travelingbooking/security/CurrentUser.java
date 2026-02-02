package com.travelingbooking.security;

import com.travelingbooking.domain.Role;

import java.util.UUID;

/**
 * Lightweight representation of the authenticated user attached to each request.
 */
public class CurrentUser {

    private final UUID id;
    private final String firebaseUid;
    private final String email;
    private final Role role;

    public CurrentUser(UUID id, String firebaseUid, String email, Role role) {
        this.id = id;
        this.firebaseUid = firebaseUid;
        this.email = email;
        this.role = role;
    }

    public UUID getId() {
        return id;
    }

    public String getFirebaseUid() {
        return firebaseUid;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }

    public boolean isSuperAdmin() {
        return Role.SUPER_ADMIN.equals(role);
    }

    public boolean isTraveler() {
        return Role.TRAVELER.equals(role);
    }

    public boolean isSupplier() {
        return Role.SUPPLIER.equals(role);
    }
}

