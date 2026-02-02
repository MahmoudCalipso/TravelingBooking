package com.travelingbooking.user;

import com.travelingbooking.domain.user.UserAccount;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class UserAccountRepository implements PanacheRepository<UserAccount> {

    public Optional<UserAccount> findByFirebaseUid(String firebaseUid) {
        return find("firebaseUid", firebaseUid).firstResultOptional();
    }

    public Optional<UserAccount> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }

    public Optional<UserAccount> findByIdOptional(UUID id) {
        return find("id", id).firstResultOptional();
    }
}

