package com.study.management.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI studyManagementOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Study Management System API")
                        .version("2.0.0")
                        .description("""
                                Clinical trial study lifecycle management.

                                **Authentication:** All endpoints (except /actuator/health and /h2-console) require a Cognito \
                                **idToken** sent as a Bearer token. Obtain it from the auth service:
                                ```
                                POST /api/auth/signin  →  { "accessToken": "...", "idToken": "...", ... }
                                ```
                                Click **Authorize** and paste the `idToken` value.

                                **Roles & permissions:**
                                | Role | Can do |
                                |---|---|
                                | `admin` | All actions |
                                | `co-investigator` | Enter and modify studies, enroll/withdraw patients |
                                | `crc` | Enter and modify studies, enroll/withdraw patients |
                                | `primary-investigator` | Create and edit study plan visits |
                                | `subject` | Read-only |

                                **Flow:** Create study → Fill visit table → Proceed for payment (optional promo code) → Study ACTIVE

                                **Promo code types:**
                                - `FULL` — 100% waiver, payment gateway is skipped entirely
                                - `PERCENTAGE` — e.g., 20% off the total (base + GST)
                                - `FLAT` — e.g., ₹1000 flat deduction

                                **Architecture:** Hexagonal (Ports & Adapters) + DDD
                                """)
                        .contact(new Contact()
                                .name("Study Management Team")
                                .email("studymgmt@example.com"))
                        .license(new License().name("MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8099").description("Local development"),
                        new Server().url("https://api.studymgmt.example.com").description("Production")))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Paste the Cognito **idToken** (not the accessToken). " +
                                             "The idToken carries the custom:role claim used for RBAC.")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}