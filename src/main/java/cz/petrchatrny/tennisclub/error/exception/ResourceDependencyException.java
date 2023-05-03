package cz.petrchatrny.tennisclub.error.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception for handling indelible resources because they are
 * dependencies to some other resources.
 */
public class ResourceDependencyException extends BaseApiException {
    public ResourceDependencyException(String message, Object dependencies) {
        super(message, HttpStatus.CONFLICT, dependencies);
    }
}
