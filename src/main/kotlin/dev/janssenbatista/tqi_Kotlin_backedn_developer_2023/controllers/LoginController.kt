package dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.controllers

import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.dtos.LoginRequestDto
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.dtos.LoginResponseDto
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.services.LoginService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/login")
@Tag(name = "Login")
class LoginController(private val loginService: LoginService) {

    @Operation(
        responses = [
            ApiResponse(description = "success login", responseCode = "200"),
            ApiResponse(description = "invalid email or password", responseCode = "400"),
        ]
    )
    @PostMapping
    fun login(@RequestBody @Valid dto: LoginRequestDto): LoginResponseDto {
        return loginService.login(dto)
    }
}