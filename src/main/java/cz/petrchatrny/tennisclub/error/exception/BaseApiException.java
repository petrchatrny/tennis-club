package cz.petrchatrny.tennisclub.error.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * Base exception class for all API exceptions
 */
@Getter
public class BaseApiException extends RuntimeException {
    private final String message;
    private final HttpStatus status;
    private final LocalDateTime timestamp;
    private final Object details;

    /**
     * @param message text message of error
     * @param status HTTP status code
     * @param details optional details of error
     */
    public BaseApiException(String message, HttpStatus status, Object details) {
        this.message = message;
        this.status = status;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }

    public BaseApiException(String message, HttpStatus status) {
        this(message, status, null);
    }
}
