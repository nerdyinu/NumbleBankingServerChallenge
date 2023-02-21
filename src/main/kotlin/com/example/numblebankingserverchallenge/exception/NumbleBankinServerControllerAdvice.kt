package com.example.numblebankingserverchallenge.exception


import com.example.numblebankingserverchallenge.exception.CustomException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.web.ErrorResponse
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.lang.Exception

@RestControllerAdvice
class NumbleBankinServerControllerAdvice : ResponseEntityExceptionHandler() {
    @ExceptionHandler(CustomException::class)
    fun handleAccountNotFoundException(ex:CustomException):ResponseEntity<*> = ResponseEntity.status(HttpStatus.valueOf(ex.code.status)).body(ex.message)
}