package com.study.management.infrastructure.web;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Temporary debug endpoint — remove before production.
 * Call with your Bearer token to see exactly what role the server extracts.
 */
@RestController
public class AuthDebugController {

    @GetMapping("/api/v1/auth/me")
    public Map<String, Object> me(Authentication auth) {
        if (auth == null) {
            return Map.of("authenticated", false);
        }
        Map<String, Object> result = new java.util.LinkedHashMap<>();
        result.put("authenticated", auth.isAuthenticated());
        result.put("principal", auth.getName());
        result.put("authorities", auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        if (auth.getCredentials() instanceof Jwt jwt) {
            result.put("token_use",   jwt.getClaimAsString("token_use"));
            result.put("custom_role", jwt.getClaimAsString("custom:role"));
            result.put("groups",      jwt.getClaimAsStringList("cognito:groups"));
            result.put("sub",         jwt.getSubject());
            result.put("phone",       jwt.getClaimAsString("phone_number"));
        }
        return result;
    }
}
