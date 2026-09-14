package com.sentinelscm.support;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MySQLContainer;

/**
 * One MySQL container for the whole test JVM (singleton container pattern).
 * Flyway applies V1 schema + V2 demo seed against it, so integration tests see the same data
 * a fresh deployment sees. Ryuk tears the container down when the JVM exits.
 */
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {

    @ServiceConnection
    static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.4")
        .withDatabaseName("scm_db")
        .withUsername("scm")
        .withPassword("scm");

    static {
        MYSQL.start();
    }
}
