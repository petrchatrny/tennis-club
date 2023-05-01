package cz.petrchatrny.tennisclub.error.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BaseApiException {
    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, null);
    }
}
