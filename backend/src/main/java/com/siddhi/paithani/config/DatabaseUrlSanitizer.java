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

            if (sanitizedUrl.startsWith("jdbc:")) {
                sanitizedUrl = sanitizedUrl.substring(5);
            }

            String scheme = "";
            if (sanitizedUrl.startsWith("postgres://")) {
                scheme = "postgresql://";
                sanitizedUrl = sanitizedUrl.substring("postgres://".length());
            } else if (sanitizedUrl.startsWith("postgresql://")) {
                scheme = "postgresql://";
                sanitizedUrl = sanitizedUrl.substring("postgresql://".length());
            } else if (sanitizedUrl.startsWith("mysql://")) {
                scheme = "mysql://";
                sanitizedUrl = sanitizedUrl.substring("mysql://".length());
            } else if (sanitizedUrl.contains("://")) {
                int idx = sanitizedUrl.indexOf("://");
                scheme = sanitizedUrl.substring(0, idx + 3);
                sanitizedUrl = sanitizedUrl.substring(idx + 3);
            }

            // Extract embedded user:password@host if present
            if (sanitizedUrl.contains("@")) {
                int atIdx = sanitizedUrl.indexOf("@");
                String userInfo = sanitizedUrl.substring(0, atIdx);
                String hostAndDb = sanitizedUrl.substring(atIdx + 1);

                if (userInfo.contains(":")) {
                    String[] userPass = userInfo.split(":", 2);
                    props.put("spring.datasource.username", userPass[0]);
                    props.put("spring.datasource.password", userPass[1]);
                } else {
                    props.put("spring.datasource.username", userInfo);
                }

                sanitizedUrl = hostAndDb;
            }

            String finalJdbcUrl = "jdbc:" + scheme + sanitizedUrl;

            // Automatically append sslmode=require for PostgreSQL if not specified
            if (finalJdbcUrl.contains("postgresql") && !finalJdbcUrl.contains("sslmode=")) {
                if (finalJdbcUrl.contains("?")) {
                    finalJdbcUrl += "&sslmode=require";
                } else {
                    finalJdbcUrl += "?sslmode=require";
                }
            }

            props.put("spring.datasource.url", finalJdbcUrl);

            // Dynamically set Driver class name based on URL type
            if (finalJdbcUrl.contains("postgresql")) {
                props.put("spring.datasource.driver-class-name", "org.postgresql.Driver");
            } else if (finalJdbcUrl.contains("mysql")) {
                props.put("spring.datasource.driver-class-name", "com.mysql.cj.jdbc.Driver");
            } else if (finalJdbcUrl.contains("h2")) {
                props.put("spring.datasource.driver-class-name", "org.h2.Driver");
            }
        }

        environment.getPropertySources().addFirst(new MapPropertySource("sanitizedDbUrlProps", props));
    }
}
