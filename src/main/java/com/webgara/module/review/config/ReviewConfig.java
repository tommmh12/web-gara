package com.webgara.module.review.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.webgara.module.review.repository")
public class ReviewConfig {
    // Configuration class for Review module
    // MongoDB repositories auto-configured via @EnableMongoRepositories
}
