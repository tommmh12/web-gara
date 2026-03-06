package com.webgara.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${app.api.version:1.0.0}")
    private String apiVersion;

    @Value("${app.api.title:Web-Gara API}")
    private String apiTitle;

    @Value("${app.api.description:Comprehensive Garage Management System}")
    private String apiDescription;

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(apiTitle)
                        .version(apiVersion)
                        .description(apiDescription + """
                                
                                ## Features
                                - **Garage Management**: CRUD operations for garage profiles
                                - **Vehicle Registry**: Customer vehicle management
                                - **Service Catalog**: Service offerings with dynamic pricing
                                - **Authentication**: JWT-based authentication with refresh tokens
                                - **User Management**: Role-based access control (CUSTOMER, RECEPTIONIST, TECHNICIAN, MANAGER)
                                - **Appointments**: Booking system with 9-state workflow
                                - **Repair Orders**: Work order management with inspection and quotes
                                - **Repair Tasks**: Task assignment and progress tracking
                                - **Parts & Inventory**: Stock management with low-stock alerts
                                - **Notifications**: Email/SMS/In-App notifications
                                - **Invoicing**: Billing with VAT calculation
                                - **Payments**: VNPay/MoMo mock integration
                                - **Reviews**: Customer feedback with 5-aspect ratings
                                
                                ## Authentication
                                Most endpoints require JWT authentication. Use the `/api/v1/auth/login` endpoint to obtain an access token.
                                Include the token in the `Authorization` header as `Bearer <token>`.
                                """)
                        .contact(new Contact()
                                .name("Web-Gara Team")
                                .url("https://github.com/tommmh12/web-gara")
                                .email("support@webgara.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Local Development Server"),
                        new Server()
                                .url("https://api.webgara.com")
                                .description("Production Server")))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Enter your JWT token obtained from the login endpoint")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
