package com.siddhi.paithani.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

/**
 * Automatically sanitizes database URLs on deployment.
 * Converts 'postgres://' or 'postgresql://' to 'jdbc:postgresql://'
 * and 'mysql://' to 'jdbc:mysql://' if the 'jdbc:' prefix was omitted.
 */
public class DatabaseUrlSanitizer implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String dbUrl = environment.getProperty("DB_URL");
        if (dbUrl == null || dbUrl.isBlank()) {
            dbUrl = environment.getProperty("DATABASE_URL");
        }

        Map<String, Object> props = new HashMap<>();

        if (dbUrl != null && !dbUrl.isBlank()) {
            String sanitizedUrl = dbUrl.trim();
            if (sanitizedUrl.startsWith("postgres://")) {
                sanitizedUrl = "jdbc:postgresql://" + sanitizedUrl.substring("postgres://".length());
            } else if (sanitizedUrl.startsWith("postgresql://")) {
                sanitizedUrl = "jdbc:postgresql://" + sanitizedUrl.substring("postgresql://".length());
            } else if (sanitizedUrl.startsWith("mysql://")) {
                sanitizedUrl = "jdbc:mysql://" + sanitizedUrl.substring("mysql://".length());
            }

            props.put("spring.datasource.url", sanitizedUrl);

            // Dynamically set Driver and Dialect based on URL type to prevent metadata lookup failures
            if (sanitizedUrl.contains("postgresql")) {
                props.put("spring.datasource.driver-class-name", "org.postgresql.Driver");
                props.put("spring.jpa.database-platform", "org.hibernate.dialect.PostgreSQLDialect");
                props.put("spring.jpa.properties.hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
            } else if (sanitizedUrl.contains("mysql")) {
                props.put("spring.datasource.driver-class-name", "com.mysql.cj.jdbc.Driver");
                props.put("spring.jpa.database-platform", "org.hibernate.dialect.MySQLDialect");
                props.put("spring.jpa.properties.hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
            } else if (sanitizedUrl.contains("h2")) {
                props.put("spring.datasource.driver-class-name", "org.h2.Driver");
                props.put("spring.jpa.database-platform", "org.hibernate.dialect.H2Dialect");
                props.put("spring.jpa.properties.hibernate.dialect", "org.hibernate.dialect.H2Dialect");
            }
        } else {
            // Default PostgreSQL settings for cloud if DB_URL is unspecified
            props.put("spring.jpa.database-platform", "org.hibernate.dialect.PostgreSQLDialect");
            props.put("spring.jpa.properties.hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        }

        environment.getPropertySources().addFirst(new MapPropertySource("sanitizedDbUrlProps", props));
    }
}
