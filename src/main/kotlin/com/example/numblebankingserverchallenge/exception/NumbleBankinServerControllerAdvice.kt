package com.example.numblebankingserverchallenge.exception


import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class NumbleBankinServerControllerAdvice : ResponseEntityExceptionHandler() {
    @ExceptionHandler(CustomException::class)
    fun handleAccountNotFoundException(ex:CustomException):ResponseEntity<*> = ResponseEntity.status(HttpStatus.valueOf(ex.code.status)).body(ex.message)
}