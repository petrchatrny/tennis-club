package cz.petrchatrny.tennisclub.error.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception for handling search of non-existing resources
 */
public class ResourceNotFoundException extends BaseApiException {
    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, null);
    }
}
