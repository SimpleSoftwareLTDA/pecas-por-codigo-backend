package org.pecasonline._exceptions

import org.pecasonline._exceptions.http.BaseExceptionResponseJson
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@ControllerAdvice
@RestControllerAdvice
class ExceptionsAdvisor {

    @ExceptionHandler(NotFoundException::class)
    fun handleGenericException(ex: Exception): ResponseEntity<BaseExceptionResponseJson> {
        val response = BaseExceptionResponseJson(HttpStatus.NOT_FOUND.value(), ex.message)
        return ResponseEntity.status(response.httpStatusCode).body(response)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: Exception): ResponseEntity<BaseExceptionResponseJson> {
        val response = BaseExceptionResponseJson(HttpStatus.BAD_REQUEST.value(), ex.message)
        return ResponseEntity.status(response.httpStatusCode).body(response)
    }
}