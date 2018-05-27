package br.com.gabryel.logineer.exceptions;

public class LogineerException extends Exception {
    private final ErrorType errorType;

    public LogineerException(ErrorType errorType, String message) {
        super(message);
        this.errorType = errorType;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public enum ErrorType {
        AUTHENTICATION, UNACCEPTABLE
    }
}
