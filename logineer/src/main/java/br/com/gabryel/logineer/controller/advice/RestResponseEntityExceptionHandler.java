package br.com.gabryel.logineer.controller.advice;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import br.com.gabryel.logineer.dto.ErrorDto;
import br.com.gabryel.logineer.dto.MultiErrorDto;
import br.com.gabryel.logineer.exceptions.LogineerException;

import static java.util.stream.Collectors.toList;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    
    @NotNull
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            @NotNull MethodArgumentNotValidException ex, @NotNull HttpHeaders headers,
            @NotNull HttpStatus status, @NotNull WebRequest request) {
        List<String> all = getErrors(ex.getBindingResult());
        
        return ResponseEntity.badRequest().body(new MultiErrorDto(all));
    }
    
    @NotNull
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            @NotNull HttpMessageNotReadableException ex, @NotNull HttpHeaders headers,
            @NotNull HttpStatus status, @NotNull WebRequest request) {
        return ResponseEntity.badRequest().body(new ErrorDto("O contéudo da mensagem REST não pode ser lido"));
    }
    
    @ExceptionHandler(value = { LogineerException.class })
    protected ResponseEntity<ErrorDto> handleConflict(LogineerException ex, WebRequest request) throws LogineerException {
        return getBodyBuilder(ex).body(new ErrorDto(ex.getMessage()));
    }
    
    @NotNull
    private List<String> getErrors(BindingResult bindingResult) {
        List<String> global = getErrors(bindingResult.getGlobalErrors(),
                error -> error.getObjectName() + " " + error.getDefaultMessage());
        
        List<String> field = getErrors(bindingResult.getFieldErrors(),
                error -> error.getField() + " " + error.getDefaultMessage());
        
        List<String> all = new ArrayList<>();
        all.addAll(global);
        all.addAll(field);
        return all;
    }
    
    private <T> List<String> getErrors(List<T> errors, Function<T, String> converter) {
        return errors.stream()
                .map(converter)
                .collect(toList());
        // Would make a concatenated string of errors, but resolved to do a list of errors.
        // .reduce("", (str1, str2) -> str1 + ";" + str2);
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