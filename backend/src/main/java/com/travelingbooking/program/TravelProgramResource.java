package com.travelingbooking.program;

import com.travelingbooking.domain.ApprovalStatus;
import com.travelingbooking.domain.Role;
import com.travelingbooking.domain.program.OrganizerType;
import com.travelingbooking.domain.program.TravelProgram;
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

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Path("/api/programs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TravelProgramResource {

    @Inject
    TravelProgramRepository travelProgramRepository;

    @Inject
    UserAccountRepository userAccountRepository;

    @Inject
    CurrentUser currentUser;

    @POST
    @RequiresRole({Role.SUPPLIER, Role.SUPER_ADMIN})
    @Transactional
    public Response create(CreateProgramRequest request) {
        UserAccount organizer = userAccountRepository.findByIdOptional(currentUser.getId())
                .orElseThrow();

        TravelProgram program = new TravelProgram();
        program.organizer = organizer;
        program.organizerType = request.organizerType;
        program.title = request.title;
        program.description = request.description;
        program.mainDestination = request.mainDestination;
        program.startDate = request.startDate;
        program.endDate = request.endDate;
        program.maxParticipants = request.maxParticipants;
        program.groupTrip = request.groupTrip;
        program.itineraryItems.addAll(request.itineraryItems);
        program.imageUrls.addAll(request.imageUrls);
        program.approvalStatus = ApprovalStatus.PENDING;

        travelProgramRepository.persist(program);
        return Response.status(Response.Status.CREATED).entity(program).build();
    }

    @GET
    @Path("/mine")
    @RequiresRole({Role.SUPPLIER, Role.SUPER_ADMIN})
    public Response mine() {
        List<TravelProgram> list = travelProgramRepository.findByOrganizer(currentUser.getId());
        return Response.ok(list).build();
    }

    @GET
    @Path("/admin/pending")
    @RequiresRole({Role.SUPER_ADMIN})
    public Response pending() {
        return Response.ok(travelProgramRepository.findPending()).build();
    }

    @PUT
    @Path("/admin/{id}/approval")
    @RequiresRole({Role.SUPER_ADMIN})
    @Transactional
    public Response approveOrReject(@PathParam("id") UUID id, ApprovalRequest request) {
        Optional<TravelProgram> programOpt = travelProgramRepository.findByIdOptional(id);
        if (programOpt.isEmpty() || programOpt.get().deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        TravelProgram program = programOpt.get();
        program.approvalStatus = request.approved ? ApprovalStatus.APPROVED : ApprovalStatus.REJECTED;
        program.updatedAt = Instant.now();
        return Response.ok(program).build();
    }

    @PUT
    @Path("/admin/{id}/delete")
    @RequiresRole({Role.SUPER_ADMIN})
    @Transactional
    public Response delete(@PathParam("id") UUID id) {
        Optional<TravelProgram> programOpt = travelProgramRepository.findByIdOptional(id);
        if (programOpt.isEmpty() || programOpt.get().deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        TravelProgram program = programOpt.get();
        program.deleted = true;
        program.updatedAt = Instant.now();
        return Response.noContent().build();
    }

    @GET
    @Path("/public")
    public Response publicPrograms(@QueryParam("from") String fromStr,
                                   @QueryParam("to") String toStr) {
        LocalDate from = fromStr != null ? LocalDate.parse(fromStr) : null;
        LocalDate to = toStr != null ? LocalDate.parse(toStr) : null;
        List<TravelProgram> list = travelProgramRepository.findPublic(from, to);
        return Response.ok(list).build();
    }

    // DTOs

    public static class CreateProgramRequest {
        @NotBlank
        public String title;
        @NotBlank
        public String description;
        @NotBlank
        public String mainDestination;
        public LocalDate startDate;
        public LocalDate endDate;
        public Integer maxParticipants;
        public boolean groupTrip = true;
        public OrganizerType organizerType = OrganizerType.OTHER;
        public Set<String> itineraryItems = new java.util.HashSet<>();
        public Set<String> imageUrls = new java.util.HashSet<>();
    }

    public static class ApprovalRequest {
        public boolean approved;
    }
}

