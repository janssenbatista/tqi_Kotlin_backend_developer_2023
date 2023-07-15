package dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.exceptions.handlers

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler

import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.LocalDateTime

@RestControllerAdvice
class ValidationExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handlerValidException(ex: MethodArgumentNotValidException): ResponseEntity<ExceptionDetails> {
        val errors: MutableMap<String, String?> = mutableMapOf()
        ex.bindingResult.allErrors.forEach { error: ObjectError ->
            val fieldName: String = (error as FieldError).field
            val messageError: String? = error.defaultMessage
            errors[fieldName] = messageError
        }
        return ResponseEntity(
                ExceptionDetails(
                        title = "Bad Request",
                        timestamp = LocalDateTime.now(),
                        details = errors
                ), HttpStatus.BAD_REQUEST
        )
    }

    data class ExceptionDetails(
            val title: String,
            val timestamp: LocalDateTime,
            val details: MutableMap<String, String?>
    )

}