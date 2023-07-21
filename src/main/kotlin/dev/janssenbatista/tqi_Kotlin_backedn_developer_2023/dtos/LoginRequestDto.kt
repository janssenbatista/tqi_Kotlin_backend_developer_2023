package dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.dtos

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class LoginRequestDto(
    @field:NotBlank(message = "email cannot be blank")
    @field:Email(message = "invalid email")
    var email: String,
    @field:NotBlank(message = "password cannot be blank")
    @field:Size(min = 8, message = "password must contain at least 8 characters")
    var password: String
)