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

        if (dbUrl != null && !dbUrl.isBlank()) {
            String sanitizedUrl = dbUrl.trim();
            if (sanitizedUrl.startsWith("postgres://")) {
                sanitizedUrl = "jdbc:postgresql://" + sanitizedUrl.substring("postgres://".length());
            } else if (sanitizedUrl.startsWith("postgresql://")) {
                sanitizedUrl = "jdbc:postgresql://" + sanitizedUrl.substring("postgresql://".length());
            } else if (sanitizedUrl.startsWith("mysql://")) {
                sanitizedUrl = "jdbc:mysql://" + sanitizedUrl.substring("mysql://".length());
            }

            Map<String, Object> props = new HashMap<>();
            props.put("spring.datasource.url", sanitizedUrl);
            environment.getPropertySources().addFirst(new MapPropertySource("sanitizedDbUrlProps", props));
        }
    }
}
