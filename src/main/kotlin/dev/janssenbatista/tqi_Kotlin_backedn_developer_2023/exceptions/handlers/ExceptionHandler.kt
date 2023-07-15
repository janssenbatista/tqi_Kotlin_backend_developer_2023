package dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.exceptions.handlers

import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.exceptions.EmployeeAlreadyExistsException
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.exceptions.EmployeeNotFoundException
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.exceptions.ForbiddenException
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
            else -> ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        }
    }
}