package br.com.gabryel.logineer.exceptions

class LogineerException(val errorType: ErrorType, message: String) : Exception(message) {
    enum class ErrorType {
        AUTHENTICATION, UNACCEPTABLE
    }
}
