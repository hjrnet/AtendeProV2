package br.com.atendepro.shared.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.cors")
public record CorsProperties(List<String> allowedOrigins) {

    public CorsProperties {
        if (allowedOrigins == null || allowedOrigins.isEmpty()) {
            allowedOrigins = List.of("http://localhost:3000", "http://127.0.0.1:3000");
        } else {
            allowedOrigins = allowedOrigins.stream()
                    .flatMap(origin -> Arrays.stream(origin.split(",")))
                    .map(String::trim)
                    .filter(origin -> !origin.isBlank())
                    .toList();
        }
    }
}
