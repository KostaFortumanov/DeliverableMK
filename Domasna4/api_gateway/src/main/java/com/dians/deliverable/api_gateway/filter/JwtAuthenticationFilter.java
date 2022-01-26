package com.dians.deliverable.api_gateway.filter;

import com.dians.deliverable.api_gateway.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Predicate;

@Component
public class JwtAuthenticationFilter implements GatewayFilter {

    private final JwtUtil jwtUtil;

    private final static List<String> publicMatchers = List.of(
            "/api/auth/login",
            "/api/auth/newAccount",
            "/ws/**"
    );

    private final static List<String> managerMatchers = List.of(
            "/api/auth/register",
            "/api/user/allDriverInfo",
            "/api/user/delete/**",
            "/api/user/edit",
            "/api/user/selectDrivers",
            "/api/jobs/unassignedJobs",
            "/api/jobs/assignedJobs",
            "/api/jobs/completedJobs",
            "/api/jobs/addJob",
            "/api/jobs/assignJobs",
            "/api/jobs/preview",
            "/api/jobs/delete/**",
            "/api/locations/**",
            "/api/notifications/**",
	    "/api/config/**"
    );

    private final static List<String> driverMatchers = List.of(
            "/api/map/allPaths",
            "/api/map/finishJob",
            "/api/map/currentJobs",
            "/api/jobs/myAssigned",
            "/api/jobs/myCompleted"
    );

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        Predicate<ServerHttpRequest> isApiSecured = r -> publicMatchers.stream()
                .noneMatch(uri -> r.getURI().getPath().equals(uri));

        Predicate<ServerHttpRequest> isDriver = r -> driverMatchers.stream()
                .anyMatch(uri -> r.getURI().getPath().equals(uri));

        Predicate<ServerHttpRequest> isManager = r -> managerMatchers.stream()
                .anyMatch(uri -> r.getURI().getPath().equals(uri));

        if (isApiSecured.test(request)) {
            if (!request.getHeaders().containsKey("Authorization")) {
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.UNAUTHORIZED);

                return response.setComplete();
            }

            String token = request.getHeaders().getOrEmpty("Authorization").get(0).substring(7);

            try {
                jwtUtil.validateToken(token);
            } catch (Exception e) {
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.BAD_REQUEST);

                return response.setComplete();
            }

            Claims claims = jwtUtil.getClaims(token);
            if((isDriver.test(request) && !claims.getSubject().equals("DRIVER"))
                    || (isManager.test(request) && !claims.getSubject().equals("MANAGER"))) {

                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.FORBIDDEN);

                return response.setComplete();
            }

            exchange.getRequest().mutate()
                    .header("userId", String.valueOf(claims.getId()))
                    .header("role", String.valueOf(claims.getSubject()))
                    .build();
        }

        return chain.filter(exchange);
    }
}
