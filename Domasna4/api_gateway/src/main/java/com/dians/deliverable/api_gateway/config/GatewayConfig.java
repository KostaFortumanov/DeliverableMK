package com.dians.deliverable.api_gateway.config;

import com.dians.deliverable.api_gateway.filter.JwtAuthenticationFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    private final JwtAuthenticationFilter filter;

    public GatewayConfig(JwtAuthenticationFilter filter) {
        this.filter = filter;
    }

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes().route("USER-SERVICE", r -> r.path("/auth/**").filters(f -> f.filter(filter)).uri("lb://USER-SERVICE"))
                .route("JOB-SERVICE", r -> r.path("/jobs/**").filters(f -> f.filter(filter)).uri("lb://JOB-SERVICE"))
                .route("echo", r -> r.path("/echo/**").filters(f -> f.filter(filter)).uri("lb://echo")).build();
    }
}
