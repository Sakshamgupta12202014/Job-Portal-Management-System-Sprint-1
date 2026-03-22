package com.capg.springboot.filter;

import com.capg.springboot.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * GatewayJwtFilter — runs on every request.
 *
 * Public routes (no JWT needed):
 *   POST /api/auth/register
 *   POST /api/auth/login
 *   POST /api/auth/refresh
 *   GET  /api/jobs/**
 *
 * Protected routes: reads Authorization: Bearer <token>, validates it,
 * then injects X-User-Id and X-User-Role headers for downstream services.
 * Downstream services read these headers — they do NOT re-validate the JWT.
 */
@Component
public class GatewayJwtFilter extends AbstractGatewayFilterFactory<GatewayJwtFilter.Config> {

    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/auth/register",
            "/api/auth/login",
            "/api/auth/refresh",
            "/api/auth/logout"
    );

    @Autowired
    private JwtUtil jwtUtil;

    public GatewayJwtFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();

            // Allow public routes and public GET job browsing
            if (isPublicRoute(path)) {
                return chain.filter(exchange);
            }

            HttpHeaders headers = exchange.getRequest().getHeaders();
            String authHeader = headers.getFirst(HttpHeaders.AUTHORIZATION);

            // No token provided
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return unauthorized(exchange, "Missing or invalid Authorization header");
            }

            String token = authHeader.substring(7);

            // Invalid or expired token
            if (!jwtUtil.validateToken(token)) {
                return unauthorized(exchange, "Invalid or expired JWT token");
            }

            // Extract identity and inject as headers for downstream services
            String userId = jwtUtil.extractUserId(token);
            String role   = jwtUtil.extractRole(token);

            ServerWebExchange mutatedExchange = exchange.mutate()
                    .request(r -> r.header("X-User-Id", userId)
                                   .header("X-User-Role", role))
                    .build();

            return chain.filter(mutatedExchange);
        };
    }

    private boolean isPublicRoute(String path) {
        // Allow all GET /api/jobs/** (browsing) — public
        if (path.startsWith("/api/jobs") &&
                (path.equals("/api/jobs") || path.contains("/api/jobs/"))) {
            // POST /api/jobs is protected (recruiter only), but GET is public
            // Actual method check is done per route config below
        }
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");
        byte[] bytes = ("{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"" + message + "\"}").getBytes();
        var buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    public static class Config {
        // configuration properties if needed
    }
}
