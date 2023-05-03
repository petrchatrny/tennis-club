package cz.petrchatrny.tennisclub.error.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception for handling already creation of already existing resources
 */
public class ResourceAlreadyExistsException extends BaseApiException {
    public ResourceAlreadyExistsException(String message, Object details) {
        super(message, HttpStatus.CONFLICT, details);
    }
}
