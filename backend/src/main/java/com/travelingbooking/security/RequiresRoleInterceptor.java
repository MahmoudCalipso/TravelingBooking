package com.travelingbooking.security;

import com.travelingbooking.domain.Role;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.ws.rs.ForbiddenException;

import java.util.Arrays;

@RequiresRole({})
@Interceptor
public class RequiresRoleInterceptor {

    @Inject
    CurrentUser currentUser;

    @AroundInvoke
    public Object checkRole(InvocationContext ctx) throws Exception {
        RequiresRole annotation = ctx.getMethod().getAnnotation(RequiresRole.class);
        if (annotation == null) {
            annotation = ctx.getTarget().getClass().getAnnotation(RequiresRole.class);
        }
        if (annotation == null) {
            return ctx.proceed();
        }

        if (currentUser == null) {
            throw new ForbiddenException("User not authenticated");
        }

        Role userRole = currentUser.getRole();
        boolean allowed = Arrays.stream(annotation.value()).anyMatch(r -> r == userRole);
        if (!allowed) {
            throw new ForbiddenException("User does not have required role");
        }

        return ctx.proceed();
    }
}

