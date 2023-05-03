package cz.petrchatrny.tennisclub.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration of JWT generator.
 * Attributes are mapped from application.properties.
 */
@Configuration
@ConfigurationProperties("jwt")
@Getter
@Setter
@NoArgsConstructor
public class JwtConfig {
    private String secret;
    private int accessTokenExpiration;
    private int refreshTokenExpiration;
}
