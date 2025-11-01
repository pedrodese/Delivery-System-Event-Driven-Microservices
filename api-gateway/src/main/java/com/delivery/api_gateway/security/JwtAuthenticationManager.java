package com.delivery.api_gateway.security;

import com.delivery.api_gateway.utils.JwtUtil;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationManager(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getCredentials().toString();

        try {
            if (Boolean.TRUE.equals(jwtUtil.validateToken(token))) {
                String username = jwtUtil.extractUsername(token);

                List<SimpleGrantedAuthority> authorities = List.of(
                        new SimpleGrantedAuthority("ROLE_USER")
                );

                return Mono.just(new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        authorities
                ));
            }
        } catch (Exception e) {
            return Mono.empty();
        }

        return Mono.empty();
    }
}