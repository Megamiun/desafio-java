package br.com.gabryel.logineer.controller.advice;

import br.com.gabryel.logineer.dto.ErrorDto;
import br.com.gabryel.logineer.exceptions.LogineerException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {LogineerException.class})
    protected ResponseEntity<ErrorDto> handleConflict(LogineerException ex, WebRequest request) throws LogineerException {
        return getBodyBuilder(ex).body(new ErrorDto(ex.getMessage()));
    }

    private ResponseEntity.BodyBuilder getBodyBuilder(LogineerException ex) throws LogineerException {
        switch (ex.getErrorType()) {
            case AUTHENTICATION:
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED);
            case UNACCEPTABLE:
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE);
            default:
                throw ex;
        }
    }
}