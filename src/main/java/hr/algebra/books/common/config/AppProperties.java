package hr.algebra.books.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Typed binding for the "app.*" config block.
 * Extended incrementally: dhmz (Feature 4), jwt (Feature 5), api (Feature 6).
 */
@ConfigurationProperties(prefix = "app")
public record AppProperties(DhmzConfig dhmz, JwtConfig jwt) {

    public record DhmzConfig(String url, Duration cacheTtl) {}

    public record JwtConfig(String secret, Duration accessTtl, Duration refreshTtl) {}
}
