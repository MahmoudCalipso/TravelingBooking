package com.travelingbooking.accommodation;

import com.travelingbooking.domain.ApprovalStatus;
import com.travelingbooking.domain.Role;
import com.travelingbooking.domain.accommodation.Accommodation;
import com.travelingbooking.domain.accommodation.AccommodationType;
import com.travelingbooking.domain.user.UserAccount;
import com.travelingbooking.security.CurrentUser;
import com.travelingbooking.security.RequiresRole;
import com.travelingbooking.user.UserAccountRepository;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Path("/api/accommodations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AccommodationResource {

    @Inject
    AccommodationRepository accommodationRepository;

    @Inject
    UserAccountRepository userAccountRepository;

    @Inject
    CurrentUser currentUser;

    // ===== Supplier endpoints =====

    @POST
    @RequiresRole({Role.SUPPLIER})
    @Transactional
    public Response create(CreateAccommodationRequest request) {
        UserAccount supplier = userAccountRepository.findByIdOptional(currentUser.getId())
                .orElseThrow();

        if (!supplier.supplierVerified) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("Supplier must be verified by Super Admin before creating accommodations")
                    .build();
        }

        Accommodation acc = new Accommodation();
        acc.supplier = supplier;
        acc.type = request.type;
        acc.title = request.title;
        acc.description = request.description;
        acc.city = request.city;
        acc.country = request.country;
        acc.latitude = request.latitude;
        acc.longitude = request.longitude;
        acc.pricePerNight = request.pricePerNight;
        acc.availableFrom = request.availableFrom;
        acc.availableTo = request.availableTo;
        acc.imageUrls.addAll(request.imageUrls);
        acc.approvalStatus = ApprovalStatus.PENDING;
        acc.visibilityFrom = request.visibilityFrom;
        acc.visibilityTo = request.visibilityTo;

        accommodationRepository.persist(acc);
        return Response.status(Response.Status.CREATED).entity(acc).build();
    }

    @GET
    @Path("/mine")
    @RequiresRole({Role.SUPPLIER})
    public Response myAccommodations() {
        List<Accommodation> list = accommodationRepository.findBySupplier(currentUser.getId());
        return Response.ok(list).build();
    }

    // ===== Admin endpoints =====

    @GET
    @Path("/admin/pending")
    @RequiresRole({Role.SUPER_ADMIN})
    public Response pending() {
        return Response.ok(accommodationRepository.findPending()).build();
    }

    @PUT
    @Path("/admin/{id}/approval")
    @RequiresRole({Role.SUPER_ADMIN})
    @Transactional
    public Response approveOrReject(@PathParam("id") UUID id, ApprovalRequest request) {
        Optional<Accommodation> accOpt = accommodationRepository.findByIdOptional(id);
        if (accOpt.isEmpty() || accOpt.get().deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        Accommodation acc = accOpt.get();
        acc.approvalStatus = request.approved ? ApprovalStatus.APPROVED : ApprovalStatus.REJECTED;
        acc.updatedAt = Instant.now();
        return Response.ok(acc).build();
    }

    @PUT
    @Path("/admin/{id}/premium")
    @RequiresRole({Role.SUPER_ADMIN})
    @Transactional
    public Response setPremium(@PathParam("id") UUID id, PremiumRequest request) {
        Optional<Accommodation> accOpt = accommodationRepository.findByIdOptional(id);
        if (accOpt.isEmpty() || accOpt.get().deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        Accommodation acc = accOpt.get();
        acc.premiumUntil = request.premiumUntil;
        acc.updatedAt = Instant.now();
        return Response.ok(acc).build();
    }

    @PUT
    @Path("/admin/{id}/delete")
    @RequiresRole({Role.SUPER_ADMIN})
    @Transactional
    public Response softDelete(@PathParam("id") UUID id) {
        Optional<Accommodation> accOpt = accommodationRepository.findByIdOptional(id);
        if (accOpt.isEmpty() || accOpt.get().deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        Accommodation acc = accOpt.get();
        acc.deleted = true;
        acc.updatedAt = Instant.now();
        return Response.noContent().build();
    }

    // ===== Public search =====

    @GET
    @Path("/public/search")
    public Response publicSearch(@QueryParam("q") String q,
                                 @QueryParam("city") String city,
                                 @QueryParam("country") String country) {
        List<Accommodation> results = accommodationRepository.searchPublic(q, city, country);
        return Response.ok(results).build();
    }

    // ===== DTOs =====

    public static class CreateAccommodationRequest {
        @NotNull
        public AccommodationType type;
        @NotBlank
        public String title;
        @NotBlank
        public String description;
        @NotBlank
        public String city;
        @NotBlank
        public String country;
        @NotNull
        public BigDecimal pricePerNight;
        public Double latitude;
        public Double longitude;
        public LocalDate availableFrom;
        public LocalDate availableTo;
        public Set<String> imageUrls = new java.util.HashSet<>();
        public Instant visibilityFrom;
        public Instant visibilityTo;
    }

    public static class ApprovalRequest {
        public boolean approved;
    }

    public static class PremiumRequest {
        public Instant premiumUntil;
    }
}

