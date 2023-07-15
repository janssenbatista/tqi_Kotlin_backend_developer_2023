package dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.dtos

import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.enums.Role
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.models.User
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

data class UserRequestDto(
        @field:NotBlank(message = "name cannot be blank")
        var name: String,
        @field:NotBlank(message = "email cannot be blank")
        @field:Email(message = "invalid email")
        var email: String,
        @field:NotBlank(message = "password cannot be blank")
        @field:Size(min = 8, message = "password must contain at least 8 characters")
        var password: String
) {
    fun toEntity(role: Role): User =
            User(
                    name = this.name,
                    email = this.email,
                    password = BCryptPasswordEncoder(12).encode(this.password),
                    roleId = role.value
            )
}

