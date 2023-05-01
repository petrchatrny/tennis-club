package cz.petrchatrny.tennisclub.error.exception;

import org.springframework.http.HttpStatus;

public class ResourceDependencyException extends BaseApiException {
    public ResourceDependencyException(String message, Object dependencies) {
        super(message, HttpStatus.CONFLICT, dependencies);
    }
}
