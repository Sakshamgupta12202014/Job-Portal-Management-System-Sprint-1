package com.capg.springboot.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private GatewayHeaderFilter gatewayHeaderFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // Step 1: Disable CSRF - not needed for REST APIs with JWT
            .csrf(csrf -> csrf.disable())

            // Step 2: No sessions - each request must carry its own identity (via headers)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Step 3: Define which endpoints are open and which are protected
            .authorizeHttpRequests(auth -> auth

                // Swagger UI and API docs are open to everyone
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/v3/api-docs/**").permitAll()

                // Health check endpoint is open
                .requestMatchers("/actuator/**").permitAll()

                // Everything else requires the user to be logged in
                .anyRequest().authenticated()
            )

            // Step 4: Run our custom filter before the default Spring Security filter
            // This is where we read X-User-Id and X-User-Role headers
            .addFilterBefore(gatewayHeaderFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
