package com.travelingbooking.admin;

import com.travelingbooking.domain.Role;
import com.travelingbooking.domain.user.UserAccount;
import com.travelingbooking.security.RequiresRole;
import com.travelingbooking.user.UserAccountRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/admin/users")
@Produces(MediaType.APPLICATION_JSON)
public class AdminUserResource {

    @Inject
    UserAccountRepository userAccountRepository;

    @GET
    @RequiresRole({Role.SUPER_ADMIN})
    public Response allUsers() {
        List<UserAccount> users = userAccountRepository.listAll();
        return Response.ok(users).build();
    }
}

