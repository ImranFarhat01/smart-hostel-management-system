package com.hostel.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Smart Hostel Management System
 * 
 * Entry point for the Spring Boot application that automates hostel operations
 * including student management, room allocation, fee tracking, complaint
 * resolution, and mess management.
 * 
 * @author Hostel Management Team
 * @version 1.0.0
 */
@SpringBootApplication(scanBasePackages = "com.hostel")
@EntityScan(basePackages = "com.hostel.model")
@EnableJpaRepositories(basePackages = "com.hostel.repository")
@EnableJpaAuditing
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        System.out.println("""
            ╔══════════════════════════════════════════════════════╗
            ║     🏨 Smart Hostel Management System v1.0.0        ║
            ║     Server running on http://localhost:8080          ║
            ║     API Docs: http://localhost:8080/api              ║
            ╚══════════════════════════════════════════════════════╝
        """);
    }
}
