package cz.petrchatrny.tennisclub.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration of Twilio SMS sender.
 * Attributes are mapped from application.properties.
 */
@Configuration
@ConfigurationProperties("twilio")
@Getter
@Setter
@NoArgsConstructor
public class TwilioConfig {
    private String accountSid;
    private String authToken;
    private String trialNumber;
}