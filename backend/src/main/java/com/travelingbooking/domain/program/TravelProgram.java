package com.travelingbooking.domain.program;

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

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "travel_program")
public class TravelProgram extends PanacheEntityBase {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    public UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id", nullable = false)
    public UserAccount organizer;

    @Enumerated(EnumType.STRING)
    @Column(name = "organizer_type", nullable = false)
    public OrganizerType organizerType;

    @Column(nullable = false)
    public String title;

    @Column(nullable = false, length = 4000)
    public String description;

    @Column(nullable = false)
    public String mainDestination;

    @Column(name = "start_date", nullable = false)
    public LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    public LocalDate endDate;

    @Column(name = "max_participants")
    public Integer maxParticipants;

    @Column(name = "is_group_trip", nullable = false)
    public boolean groupTrip = true;

    @ElementCollection
    @CollectionTable(name = "travel_program_itinerary", joinColumns = @JoinColumn(name = "program_id"))
    @Column(name = "item")
    public Set<String> itineraryItems = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "travel_program_image", joinColumns = @JoinColumn(name = "program_id"))
    @Column(name = "image_url")
    public Set<String> imageUrls = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status", nullable = false)
    public ApprovalStatus approvalStatus = ApprovalStatus.PENDING;

    @Column(name = "created_at", nullable = false, updatable = false)
    public Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    public Instant updatedAt = Instant.now();

    @Column(name = "is_deleted", nullable = false)
    public boolean deleted = false;
}

