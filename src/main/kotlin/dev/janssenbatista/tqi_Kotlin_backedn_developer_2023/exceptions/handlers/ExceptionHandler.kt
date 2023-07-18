package dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.exceptions.handlers

import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.exceptions.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ExceptionHandler {

    @ExceptionHandler(RuntimeException::class)
    fun handleException(ex: RuntimeException): ResponseEntity<Any> {
        return when (ex) {
            is ForbiddenException -> ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.message)
            is EmployeeAlreadyExistsException -> ResponseEntity.status(HttpStatus.CONFLICT).body(ex.message)
            is EmployeeNotFoundException -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.message)
            is CustomerAlreadyExistsException -> ResponseEntity.status(HttpStatus.CONFLICT).body(ex.message)
            is CustomerNotFoundException -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.message)
            is CategoryAlreadyExistsException -> ResponseEntity.status(HttpStatus.CONFLICT).body(ex.message)
            is CategoryNotFoundException -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.message)
            is ProductAlreadyExistsException -> ResponseEntity.status(HttpStatus.CONFLICT).body(ex.message)
            is ProductNotFoundException -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.message)
            is ConstraintViolationException -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.message)
            else -> ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        }
    }
}