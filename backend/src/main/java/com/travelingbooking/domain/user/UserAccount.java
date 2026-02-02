package com.travelingbooking.domain.user;

import com.travelingbooking.domain.Role;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.GenericGenerator;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "user_account")
public class UserAccount extends PanacheEntityBase {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    public UUID id;

    /**
     * Firebase Authentication UID
     */
    @Column(name = "firebase_uid", nullable = false, unique = true, updatable = false)
    public String firebaseUid;

    @Email
    @NotBlank
    @Column(nullable = false, unique = true)
    public String email;

    @Column(name = "display_name")
    public String displayName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public Role role;

    @Column(nullable = false)
    public String status; // ACTIVE, PENDING, BLOCKED

    // Traveler profile fields
    public LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    public Gender gender;

    @Enumerated(EnumType.STRING)
    public WorkStatus workStatus;

    @Enumerated(EnumType.STRING)
    public DrivingLicenseCategory drivingLicenseCategory = DrivingLicenseCategory.NONE;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "user_hobby", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "hobby")
    public Set<String> hobbies = new HashSet<>();

    @Column(name = "profile_photo_url")
    public String profilePhotoUrl;

    // Supplier-specific fields
    @Column(name = "supplier_display_name")
    public String supplierDisplayName;

    @Column(name = "supplier_phone")
    public String supplierPhone;

    @Column(name = "supplier_verified")
    public boolean supplierVerified;

    @Column(name = "visibility_plan")
    public String visibilityPlan; // e.g. FREE, PREMIUM_HOMEPAGE, etc.

    // Privacy settings (simple key/value booleans)
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "user_privacy_setting", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "value")
    public Set<String> privacyFlags = new HashSet<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    public Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    public Instant updatedAt = Instant.now();

    public boolean isSuperAdmin() {
        return Role.SUPER_ADMIN.equals(this.role);
    }

    public boolean isTraveler() {
        return Role.TRAVELER.equals(this.role);
    }

    public boolean isSupplier() {
        return Role.SUPPLIER.equals(this.role);
    }
}

