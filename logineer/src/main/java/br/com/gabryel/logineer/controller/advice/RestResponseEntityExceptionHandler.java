package br.com.gabryel.logineer.controller.advice;

import br.com.gabryel.logineer.dto.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {IllegalArgumentException.class})
    protected ResponseEntity<ErrorDto> handleConflict(IllegalArgumentException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ErrorDto(ex.getMessage()));
    }
}