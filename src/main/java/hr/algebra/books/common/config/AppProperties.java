package hr.algebra.books.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Typed binding for the "app.*" config block.
 * Extended incrementally: dhmz (F4), jwt (F5), api (F6).
 * Note: api.mode is String (not ApiMode enum) to avoid common/ depending on book/.
 */
@ConfigurationProperties(prefix = "app")
public record AppProperties(DhmzConfig dhmz, JwtConfig jwt, ApiConfig api) {

    public record DhmzConfig(String url, Duration cacheTtl) {}

    public record JwtConfig(String secret, Duration accessTtl, Duration refreshTtl) {}

    public record ApiConfig(String mode, String publicBaseUrl) {}
}
