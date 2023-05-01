package cz.petrchatrny.tennisclub.error.exception;

import org.springframework.http.HttpStatus;

public class UnexpectedUserInputException extends BaseApiException {
    public UnexpectedUserInputException(String message, Object details) {
        super(message, HttpStatus.UNPROCESSABLE_ENTITY, details);
    }
}
