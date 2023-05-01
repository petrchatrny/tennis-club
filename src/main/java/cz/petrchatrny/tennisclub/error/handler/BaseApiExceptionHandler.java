package cz.petrchatrny.tennisclub.error.handler;

import cz.petrchatrny.tennisclub.error.exception.BaseApiException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class BaseApiExceptionHandler {
    @ExceptionHandler(value = BaseApiException.class)
    public ResponseEntity<Object> handleBaseException(BaseApiException ex) {
        return new ResponseEntity<>(ex, ex.getStatus());
    }
}
