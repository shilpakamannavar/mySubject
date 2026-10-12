package com.study.management.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Converts a Cognito idToken JWT into a Spring Security authentication token.
 *
 * Cognito stores the user's role as the custom attribute "custom:role" inside
 * the idToken (values: admin, co-investigator, crc, primary-investigator, subject).
 * This converter maps those values to Spring Security ROLE_ authorities so that
 * @PreAuthorize("hasRole(...)") expressions work on every controller method.
 *
 * Clients must send the Cognito idToken (not the accessToken) as the Bearer token:
 *   Authorization: Bearer <idToken>
 */
@Component
public class CognitoJwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private static final Logger log = LoggerFactory.getLogger(CognitoJwtConverter.class);

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = extractAuthorities(jwt);
        log.warn("[AUTH] sub={} tokenUse={} customRole={} groups={} → authorities={}",
                jwt.getSubject(),
                jwt.getClaimAsString("token_use"),
                jwt.getClaimAsString("custom:role"),
                jwt.getClaimAsStringList("cognito:groups"),
                authorities);
        return new JwtAuthenticationToken(jwt, authorities, resolvePrincipal(jwt));
    }

    // ── private helpers ──────────────────────────────────────────────────────

    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        // idToken carries custom:role; accessToken does not
        String role = jwt.getClaimAsString("custom:role");
        if (role != null && !role.isBlank()) {
            return List.of(new SimpleGrantedAuthority(
                "ROLE_" + role.toUpperCase().replace("-", "_")));
        }
        // accessToken carries cognito:groups — used for admin promo-code endpoints
        List<String> groups = jwt.getClaimAsStringList("cognito:groups");
        if (groups != null && !groups.isEmpty()) {
            return groups.stream()
                .map(g -> new SimpleGrantedAuthority(
                    "ROLE_" + g.toUpperCase().replace("-", "_")))
                .collect(Collectors.toList());
        }
        return List.of();
    }

    private String resolvePrincipal(Jwt jwt) {
        String phone = jwt.getClaimAsString("phone_number");
        if (phone != null && !phone.isBlank()) return phone;
        String email = jwt.getClaimAsString("email");
        if (email != null && !email.isBlank()) return email;
        return jwt.getSubject();
    }
}
