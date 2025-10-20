package com.delivery.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/auth/**").permitAll()
                        .pathMatchers("/actuator/**").permitAll()
                        .pathMatchers("/eureka/**").permitAll()

                        .pathMatchers(HttpMethod.GET, "/api/v1/orders/**").authenticated()
                        .pathMatchers(HttpMethod.POST, "/api/v1/orders/**").authenticated()
                        .pathMatchers(HttpMethod.PATCH, "/api/v1/orders/**").authenticated()
                        .pathMatchers(HttpMethod.DELETE, "/api/v1/orders/**").authenticated()

                        .anyExchange().authenticated()
                )
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .build();
    }
}