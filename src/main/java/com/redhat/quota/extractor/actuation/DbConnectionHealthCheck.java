package com.redhat.quota.extractor.actuation;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Readiness;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * at /q/health/ready
 */
@Readiness
@ApplicationScoped
@Slf4j
public class DbConnectionHealthCheck implements HealthCheck {

    @Inject
    DataSource dataSource;

    @Override
    public HealthCheckResponse call() {
        HealthCheckResponseBuilder responseBuilder = HealthCheckResponse.named("db connection health check");
        try {
            dataSource.getConnection();
            responseBuilder.up();
        } catch (SQLException e) {
            log.error("ready health check: database connection error, SQLSTATE=" +
                    e.getSQLState() + ", SQLCODE=" + e.getErrorCode());
            responseBuilder.down();
        }
        return responseBuilder.build();
    }

}
