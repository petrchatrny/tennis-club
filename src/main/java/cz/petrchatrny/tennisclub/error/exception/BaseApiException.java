package cz.petrchatrny.tennisclub.error.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
public class BaseApiException extends RuntimeException {
    private final String message;
    private final HttpStatus status;
    private final LocalDateTime timestamp;
    private final Object details;

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
