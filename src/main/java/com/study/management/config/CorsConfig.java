package com.study.management.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Global CORS policy.
 *
 * This bean is wired into the Spring Security filter chain (SecurityConfig), not as
 * a servlet Filter, so it runs before any authentication check.  That means the
 * browser's OPTIONS preflight is answered with the correct headers even on protected
 * routes — without exposing those routes as unauthenticated.
 *
 * Allowed origins are read from application.properties so the same JAR runs in
 * dev (localhost) and prod (real domain) without code changes.
 */
@Configuration
public class CorsConfig {

    /**
     * Comma-separated list of allowed frontend origins.
     * Default covers the most common local dev ports; override in prod via env var or properties.
     *
     * Example application.properties:
     *   cors.allowed-origins=http://localhost:3000,http://localhost:5173
     * Example production:
     *   cors.allowed-origins=https://app.myclinicalplatform.com
     */
    @Value("${cors.allowed-origins:*}")
    private String allowedOriginsPattern;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // allowedOriginPatterns supports "*" alongside allowCredentials=true
        config.setAllowedOriginPatterns(List.of(allowedOriginsPattern));

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // Allow the Authorization header so the frontend can send Bearer tokens
        config.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type",
                "Accept",
                "X-Requested-With",
                "Cache-Control"
        ));

        // Expose Authorization in responses so the frontend can read it if needed
        config.setExposedHeaders(List.of("Authorization"));

        // Required for the browser to include cookies / Authorization header on cross-origin requests
        config.setAllowCredentials(true);

        // Cache preflight result for 1 hour — reduces OPTIONS round-trips
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
