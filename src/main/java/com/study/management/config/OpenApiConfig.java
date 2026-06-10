package com.study.management.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
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
                        new Server().url("http://localhost:8080").description("Local development"),
                        new Server().url("https://api.studymgmt.example.com").description("Production")));
    }
}