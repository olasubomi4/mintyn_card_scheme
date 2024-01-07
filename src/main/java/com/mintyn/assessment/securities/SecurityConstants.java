package com.mintyn.assessment.securities;

public class SecurityConstants {

    // Authorization : "Bearer " + Token
    public static final String BEARER = "Bearer ";
    // "Authorization" : Bearer Token
    public static final String AUTHORIZATION = "Authorization";
    // Public path that clients can use to register.
    public static final String REGISTER_PATH = "/api/v1/user/register";
    //OpenApi documentation endpoints
    public static final String[] AUTH_WHITELIST = {
            // -- Swagger UI v2
            "/v2/api-docs",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",
            "/v3/api-docs/**",
            "/swagger-ui/**"
    };
}