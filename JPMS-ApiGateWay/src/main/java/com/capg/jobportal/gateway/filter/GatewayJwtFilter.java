package com.capg.jobportal.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.capg.jobportal.gateway.util.JwtUtil;

import reactor.core.publisher.Mono;
 
import java.util.List;
 

@Component
public class GatewayJwtFilter implements GlobalFilter , Ordered {

	private final JwtUtil jwtUtil;
	 
    private static final List<String> PUBLIC_ROUTES = List.of(
            "/api/auth/register",
            "/api/auth/login",
            "/api/auth/refresh"
    );
 
    private static final List<String> PUBLIC_GET_ROUTES = List.of(
            "/api/jobs"
    );
    
    private static final List<String> ADMIN_ONLY_ROUTES = List.of(
			"/api/admin"
	);
    
    
    public GatewayJwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }    
    
	
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        String method = request.getMethod().name();
 
        if (isPublicRoute(path, method)) {
            return chain.filter(exchange);
        }
 
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
 
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return onError(exchange, HttpStatus.UNAUTHORIZED);
        }
 
        String token = authHeader.substring(7);
 
        if (!jwtUtil.isTokenValid(token)) {
            return onError(exchange, HttpStatus.UNAUTHORIZED);
        }
 
        String userId = jwtUtil.extractUserId(token);
        String role = jwtUtil.extractRole(token);

        // Block non-ADMIN users from admin routes at the gateway level
        if (isAdminRoute(path) && !"ADMIN".equalsIgnoreCase(role)) {
            return onError(exchange, HttpStatus.FORBIDDEN);
        }

        ServerHttpRequest modifiedRequest = request.mutate()
                .header("X-User-Id", userId)
                .header("X-User-Role", role)
                .build();
 
        return chain.filter(exchange.mutate().request(modifiedRequest).build());
    }
    
 
    private boolean isPublicRoute(String path, String method) {
        if (PUBLIC_ROUTES.stream().anyMatch(path::startsWith)) {
            return true;
        }
        if ("GET".equalsIgnoreCase(method) &&
                PUBLIC_GET_ROUTES.stream().anyMatch(path::startsWith)) {
            return true;
        }
        return false;
    }

    private boolean isAdminRoute(String path) {
        return ADMIN_ONLY_ROUTES.stream().anyMatch(path::startsWith);
    }
 
    
    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        return response.setComplete();
    }
 
    
    @Override
    public int getOrder() {
        return -1;
    }
	
}
