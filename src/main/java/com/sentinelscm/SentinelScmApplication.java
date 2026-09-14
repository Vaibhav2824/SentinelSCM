package com.sentinelscm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * SentinelSCM entry point.
 *
 * Architecture: Thymeleaf UI + JSON API (Spring MVC) -> Services -> Spring Data JPA -> MySQL.
 * Design patterns on display: Strategy (risk algorithms), Observer (Spring events),
 * Factory (AlertFactory), Builder (ReportBuilder), Singleton (container-managed beans).
 */
@SpringBootApplication
public class SentinelScmApplication {

    public static void main(String[] args) {
        SpringApplication.run(SentinelScmApplication.class, args);
    }
}
