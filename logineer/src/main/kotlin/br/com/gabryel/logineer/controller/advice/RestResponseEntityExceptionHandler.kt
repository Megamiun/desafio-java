package br.com.gabryel.logineer.controller.advice

import br.com.gabryel.logineer.dto.ErrorDto
import br.com.gabryel.logineer.dto.MultiErrorDto
import br.com.gabryel.logineer.exceptions.LogineerException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.BindingResult
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class RestResponseEntityExceptionHandler : ResponseEntityExceptionHandler() {

    override fun handleMethodArgumentNotValid(
            ex: MethodArgumentNotValidException, headers: HttpHeaders,
            status: HttpStatus, request: WebRequest
    ): ResponseEntity<Any> {

        val all = getAllErrors(ex.bindingResult)
        return ResponseEntity.badRequest().body(MultiErrorDto(all))
    }

    override fun handleHttpMessageNotReadable(
            ex: HttpMessageNotReadableException, headers: HttpHeaders,
            status: HttpStatus, request: WebRequest
    ): ResponseEntity<Any> =
            ResponseEntity.badRequest().body(ErrorDto("O contéudo da mensagem REST não pode ser lido"))

    @ExceptionHandler(value = [(LogineerException::class)])
    @Throws(LogineerException::class)
    fun handleConflict(ex: LogineerException, request: WebRequest): ResponseEntity<ErrorDto> {
        val message = ex.message?: "Unknown error"
        return getBodyBuilder(ex).body(ErrorDto(message))
    }

    private fun getAllErrors(bindingResult: BindingResult) =
            getFieldErrors(bindingResult) + getGlobalErrors(bindingResult)

    private fun getFieldErrors(bindingResult: BindingResult) =
            getAllErrors(bindingResult.fieldErrors) { "${it.field} ${it.defaultMessage}" }

    private fun getGlobalErrors(bindingResult: BindingResult) =
            getAllErrors(bindingResult.globalErrors) { "${it.objectName} ${it.defaultMessage}" }

    private fun <T> getAllErrors(errors: List<T>, converter: (T) -> String) = errors.map(converter)

    @Throws(LogineerException::class)
    private fun getBodyBuilder(ex: LogineerException): ResponseEntity.BodyBuilder {
        return when (ex.errorType) {
            LogineerException.ErrorType.AUTHENTICATION -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            LogineerException.ErrorType.UNACCEPTABLE -> ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
        }
    }
}