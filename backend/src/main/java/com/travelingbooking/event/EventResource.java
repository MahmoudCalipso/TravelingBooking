package com.travelingbooking.event;

import com.travelingbooking.domain.ApprovalStatus;
import com.travelingbooking.domain.Role;
import com.travelingbooking.domain.event.Event;
import com.travelingbooking.domain.user.UserAccount;
import com.travelingbooking.security.CurrentUser;
import com.travelingbooking.security.RequiresRole;
import com.travelingbooking.user.UserAccountRepository;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
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

@Path("/api/events")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EventResource {

    @Inject
    EventRepository eventRepository;

    @Inject
    UserAccountRepository userAccountRepository;

    @Inject
    CurrentUser currentUser;

    // Authorized entities create events (associations, startups, schools, faculties).
    // Here we treat them as suppliers or special verified organizers at user level.

    @POST
    @RequiresRole({Role.SUPPLIER, Role.SUPER_ADMIN})
    @Transactional
    public Response create(CreateEventRequest request) {
        UserAccount organizer = userAccountRepository.findByIdOptional(currentUser.getId())
                .orElseThrow();

        Event ev = new Event();
        ev.organizer = organizer;
        ev.title = request.title;
        ev.description = request.description;
        ev.locationName = request.locationName;
        ev.latitude = request.latitude;
        ev.longitude = request.longitude;
        ev.startDate = request.startDate;
        ev.endDate = request.endDate;
        ev.price = request.price;
        ev.organizerName = request.organizerName;
        ev.organizerContact = request.organizerContact;
        ev.imageUrls.addAll(request.imageUrls);
        ev.approvalStatus = ApprovalStatus.PENDING;

        eventRepository.persist(ev);
        return Response.status(Response.Status.CREATED).entity(ev).build();
    }

    @GET
    @Path("/mine")
    @RequiresRole({Role.SUPPLIER, Role.SUPER_ADMIN})
    public Response mine() {
        List<Event> list = eventRepository.findByOrganizer(currentUser.getId());
        return Response.ok(list).build();
    }

    @GET
    @Path("/admin/pending")
    @RequiresRole({Role.SUPER_ADMIN})
    public Response pending() {
        return Response.ok(eventRepository.findPending()).build();
    }

    @PUT
    @Path("/admin/{id}/approval")
    @RequiresRole({Role.SUPER_ADMIN})
    @Transactional
    public Response approveOrReject(@PathParam("id") UUID id, ApprovalRequest request) {
        Optional<Event> evOpt = eventRepository.findByIdOptional(id);
        if (evOpt.isEmpty() || evOpt.get().deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        Event ev = evOpt.get();
        ev.approvalStatus = request.approved ? ApprovalStatus.APPROVED : ApprovalStatus.REJECTED;
        ev.updatedAt = Instant.now();
        return Response.ok(ev).build();
    }

    @PUT
    @Path("/admin/{id}/delete")
    @RequiresRole({Role.SUPER_ADMIN})
    @Transactional
    public Response delete(@PathParam("id") UUID id) {
        Optional<Event> evOpt = eventRepository.findByIdOptional(id);
        if (evOpt.isEmpty() || evOpt.get().deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        Event ev = evOpt.get();
        ev.deleted = true;
        ev.updatedAt = Instant.now();
        return Response.noContent().build();
    }

    // Public

    @GET
    @Path("/public")
    public Response publicEvents(@QueryParam("from") String fromStr,
                                 @QueryParam("to") String toStr) {
        LocalDate from = fromStr != null ? LocalDate.parse(fromStr) : null;
        LocalDate to = toStr != null ? LocalDate.parse(toStr) : null;
        List<Event> list = eventRepository.findPublicActive(from, to);
        return Response.ok(list).build();
    }

    // DTOs

    public static class CreateEventRequest {
        @NotBlank
        public String title;
        @NotBlank
        public String description;
        @NotBlank
        public String locationName;
        public Double latitude;
        public Double longitude;
        public LocalDate startDate;
        public LocalDate endDate;
        public BigDecimal price;
        public String organizerName;
        public String organizerContact;
        public Set<String> imageUrls = new java.util.HashSet<>();
    }

    public static class ApprovalRequest {
        public boolean approved;
    }
}

