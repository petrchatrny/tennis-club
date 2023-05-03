package cz.petrchatrny.tennisclub.error.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception for handling user's invalid or unacceptable input
 */
public class UnexpectedUserInputException extends BaseApiException {
    public UnexpectedUserInputException(String message, Object details) {
        super(message, HttpStatus.UNPROCESSABLE_ENTITY, details);
    }
}
