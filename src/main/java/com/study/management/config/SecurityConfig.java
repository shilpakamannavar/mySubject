package com.study.management.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;


import java.util.List;

/**
 * Stateless JWT security configuration.
 *
 * Token flow:
 *   1. Client calls /api/auth/signin on the auth service (port 8090) → receives idToken.
 *   2. Client includes the idToken on every subsequent request:
 *        Authorization: Bearer <idToken>
 *   3. Spring validates the JWT signature via Cognito JWKS, checks expiry, issuer, and
 *      audience (client ID), then hands it to CognitoJwtConverter which reads custom:role.
 *
 * Role → authority mapping (handled by CognitoJwtConverter):
 *   admin               → ROLE_ADMIN
 *   co-investigator     → ROLE_CO_INVESTIGATOR
 *   crc                 → ROLE_CRC
 *   primary-investigator → ROLE_PRIMARY_INVESTIGATOR
 *   subject             → ROLE_SUBJECT
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    @Value("${app.cognito.client-id}")
    private String clientId;

    private final CognitoJwtConverter    cognitoJwtConverter;
    private final CorsConfigurationSource corsConfigurationSource;

    public SecurityConfig(CognitoJwtConverter    cognitoJwtConverter,
                          CorsConfigurationSource corsConfigurationSource) {
        this.cognitoJwtConverter    = cognitoJwtConverter;
        this.corsConfigurationSource = corsConfigurationSource;
    }
/***
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .headers(headers -> headers.frameOptions(fo -> fo.disable()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/actuator/health",
                    "/actuator/info",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**",
                    "/api/razorpay/webhook",
                    "/api/razorpay/callback"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(cognitoJwtConverter))
            );
        return http.build();
    }
**/
@Bean
@Order(1) // Tells Spring to check this rule first
public SecurityFilterChain h2ConsoleSecurityFilterChain(HttpSecurity http) throws Exception {
    http
            .securityMatcher("/h2-console/**") // Only handles H2 console requests
            .authorizeHttpRequests(auth -> auth
                    .anyRequest().permitAll() // Allows anyone to hit the login layout pages
            )
            .csrf(AbstractHttpConfigurer::disable // Disables form check rules for H2 layout pages
            )
            .headers(headers -> headers
                    .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin) // Allows H2 to load its screen frames
            );

    return http.build();
}

@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .csrf(csrf -> csrf
                    .ignoringRequestMatchers("/h2-console/**")
                    .disable()
            )
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .headers(headers -> headers
                    .frameOptions(fo -> fo.sameOrigin())
            )
            .authorizeHttpRequests(auth -> auth
                    // FIX: Changed from PathRequest to a simple string path
                    .requestMatchers("/h2-console/**").permitAll()
                    .requestMatchers(
                            "/error",
                            "/actuator/health",
                            "/actuator/info",
                            "/swagger-ui/**",
                            "/swagger-ui.html",
                            "/v3/api-docs/**",
                            "/api/razorpay/webhook",
                            "/api/razorpay/callback",
                            "/api/billing/webhook/razorpay",   // Razorpay server → HMAC-verified, no JWT
                            "/api/billing/payment/callback",   // Razorpay browser redirect → no JWT
                            "/api/v1/investigators",
                            "/api/v1/investigators/**",
                            "/api/v1/co-investigators",
                            "/api/v1/co-investigators/**",
                            "/api/v1/crcs",
                            "/api/v1/crcs/**",
                            "/api/v1/cros",
                            "/api/v1/cros/**",
                            "/api/v1/hospitals",
                            "/api/v1/hospitals/**",
                            "/api/v1/auth/users/*/confirmation-status",
                            "/api/v1/auth/phone/signup",
                            "/api/v1/auth/phone/signup/verify",
                            "/api/v1/auth/phone/signin",
                            "/api/billing/gstin/validate",
                            "/api/v1/visit-types",
                            "/api/v1/visit-types/**"
                    ).permitAll()
                    .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                    .jwt(jwt -> jwt.jwtAuthenticationConverter(cognitoJwtConverter))
            );

    return http.build();
}


    /**
     * Custom JwtDecoder that validates:
     *  - JWT signature (Cognito JWKS)
     *  - Issuer (Cognito User Pool URL)
     *  - Expiry and not-before
     *  - Audience (must match the Cognito app client ID from the idToken's "aud" claim)
     *
     * Uses withJwkSetUri() instead of withIssuerLocation() so no HTTP call is made
     * at startup — the JWKS is fetched lazily on the first token validation.
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        String jwksUri = issuerUri + "/.well-known/jwks.json";
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(jwksUri).build();

        OAuth2TokenValidator<Jwt> audienceValidator = jwt -> {
            List<String> audiences = jwt.getAudience();
            if (audiences != null && audiences.contains(clientId)) {
                return OAuth2TokenValidatorResult.success();
            }
            // Cognito accessToken carries client_id instead of aud
            String tokenClientId = jwt.getClaimAsString("client_id");
            if (clientId.equals(tokenClientId)) {
                return OAuth2TokenValidatorResult.success();
            }
            return OAuth2TokenValidatorResult.failure(
                new OAuth2Error("invalid_token",
                    "JWT audience does not match Cognito app client ID", null));
        };

        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
            JwtValidators.createDefaultWithIssuer(issuerUri),
            audienceValidator
        ));

        return decoder;
    }
}
