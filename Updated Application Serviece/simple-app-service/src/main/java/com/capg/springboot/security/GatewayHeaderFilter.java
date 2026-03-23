package com.capg.springboot.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// This filter runs on every single request that comes in
// The API Gateway already validated the JWT and added two headers:
//    X-User-Id   = the user's id (example: "42")
//    X-User-Role = the user's role (example: "JOB_SEEKER")
//
// This filter reads those headers and tells Spring Security who the user is
// We do NOT validate the JWT here - that is the gateway's job

@Component
public class GatewayHeaderFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // Step 1: Read the headers that the gateway injected
        String userId = request.getHeader("X-User-Id");
        String role   = request.getHeader("X-User-Role");

        // Step 2: If both headers are present, tell Spring Security who this user is
        if (userId != null && role != null) {

            // Create a list of authorities (permissions) for this user
            // Spring Security needs the "ROLE_" prefix before the role name
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role));

            // Create the authentication object with userId as the principal
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userId, null, authorities);

            // Put it in the SecurityContext so Spring Security knows the user is logged in
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // Step 3: Continue to the next filter or to the controller
        filterChain.doFilter(request, response);
    }

}
