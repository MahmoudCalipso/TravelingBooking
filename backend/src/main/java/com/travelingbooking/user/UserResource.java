package com.travelingbooking.user;

import com.travelingbooking.domain.Role;
import com.travelingbooking.domain.user.DrivingLicenseCategory;
import com.travelingbooking.domain.user.Gender;
import com.travelingbooking.domain.user.UserAccount;
import com.travelingbooking.domain.user.WorkStatus;
import com.travelingbooking.security.CurrentUser;
import com.travelingbooking.security.RequiresRole;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDate;
import java.util.Optional;

@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    UserAccountRepository userAccountRepository;

    @Inject
    CurrentUser currentUser;

    /**
     * Traveler or Supplier calls this once after Firebase signup to register profile & role.
     */
    @POST
    @Path("/register")
    @Transactional
    public Response register(RegisterRequest request) {
        Optional<UserAccount> existing = userAccountRepository.findByFirebaseUid(request.firebaseUid);
        if (existing.isPresent()) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("User already registered")
                    .build();
        }

        if (request.role == Role.SUPER_ADMIN) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("Cannot self-register as SUPER_ADMIN")
                    .build();
        }

        UserAccount user = new UserAccount();
        user.firebaseUid = request.firebaseUid;
        user.email = request.email;
        user.displayName = request.displayName;
        user.role = request.role;
        user.status = "PENDING"; // requires admin validation, especially for suppliers

        user.dateOfBirth = request.dateOfBirth;
        user.gender = request.gender;
        user.workStatus = request.workStatus;
        user.drivingLicenseCategory = request.drivingLicenseCategory != null
                ? request.drivingLicenseCategory
                : DrivingLicenseCategory.NONE;
        user.hobbies.addAll(request.hobbies);
        user.profilePhotoUrl = request.profilePhotoUrl;

        if (user.isSupplier()) {
            user.supplierDisplayName = request.supplierDisplayName;
            user.supplierPhone = request.supplierPhone;
            user.supplierVerified = false;
            user.visibilityPlan = "FREE";
        }

        userAccountRepository.persist(user);
        return Response.status(Response.Status.CREATED).entity(user).build();
    }

    /**
     * Get currently authenticated user profile.
     */
    @GET
    @Path("/me")
    public Response me() {
        Optional<UserAccount> userOpt = userAccountRepository.findByIdOptional(currentUser.getId());
        if (userOpt.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(userOpt.get()).build();
    }

    /**
     * Super Admin validates a supplier or traveler.
     */
    @PUT
    @Path("/admin/activate")
    @RequiresRole({Role.SUPER_ADMIN})
    @Transactional
    public Response activateUser(ActivateUserRequest request) {
        Optional<UserAccount> userOpt = userAccountRepository.findByIdOptional(request.userId);
        if (userOpt.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        UserAccount user = userOpt.get();
        user.status = request.active ? "ACTIVE" : "BLOCKED";
        if (user.isSupplier()) {
            user.supplierVerified = request.active;
        }
        return Response.ok(user).build();
    }

    public static class RegisterRequest {
        @NotBlank
        public String firebaseUid;
        @NotBlank
        public String email;
        public String displayName;
        public Role role;

        // Traveler fields
        public LocalDate dateOfBirth;
        public Gender gender;
        public WorkStatus workStatus;
        public DrivingLicenseCategory drivingLicenseCategory;
        public java.util.Set<String> hobbies = new java.util.HashSet<>();
        public String profilePhotoUrl;

        // Supplier fields
        public String supplierDisplayName;
        public String supplierPhone;
    }

    public static class ActivateUserRequest {
        public java.util.UUID userId;
        public boolean active;
    }
}

