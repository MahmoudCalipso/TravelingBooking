package com.travelingbooking.domain.accommodation;

import com.travelingbooking.domain.ApprovalStatus;
import com.travelingbooking.domain.user.UserAccount;
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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "accommodation")
public class Accommodation extends PanacheEntityBase {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    public UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    public UserAccount supplier;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    public AccommodationType type;

    @Column(nullable = false)
    public String title;

    @Column(nullable = false, length = 4000)
    public String description;

    @Column(nullable = false)
    public String city;

    @Column(nullable = false)
    public String country;

    public Double latitude;
    public Double longitude;

    @Column(name = "price_per_night", nullable = false)
    public BigDecimal pricePerNight;

    @ElementCollection
    @CollectionTable(name = "accommodation_image", joinColumns = @JoinColumn(name = "accommodation_id"))
    @Column(name = "image_url")
    public Set<String> imageUrls = new HashSet<>();

    @Column(name = "available_from")
    public LocalDate availableFrom;

    @Column(name = "available_to")
    public LocalDate availableTo;

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status", nullable = false)
    public ApprovalStatus approvalStatus = ApprovalStatus.PENDING;

    @Column(name = "visibility_from")
    public Instant visibilityFrom;

    @Column(name = "visibility_to")
    public Instant visibilityTo;

    @Column(name = "premium_until")
    public Instant premiumUntil;

    @Column(name = "is_deleted", nullable = false)
    public boolean deleted = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    public Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    public Instant updatedAt = Instant.now();

    public boolean isPremiumVisible() {
        return premiumUntil != null && premiumUntil.isAfter(Instant.now());
    }

    public boolean isCurrentlyVisible() {
        Instant now = Instant.now();
        boolean withinVisibilityWindow =
                (visibilityFrom == null || !now.isBefore(visibilityFrom)) &&
                (visibilityTo == null || !now.isAfter(visibilityTo));
        return !deleted && approvalStatus == ApprovalStatus.APPROVED && withinVisibilityWindow;
    }
}

