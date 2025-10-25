package com.delivery.authservice.constants;

public class SecurityConstants {
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String[] PUBLIC_ENDPOINTS = {
            "/auth/register",
            "/auth/login",
            "/actuator/**"
    };
}