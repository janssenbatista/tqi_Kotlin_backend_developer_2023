package dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.controllers

import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.dtos.LoginRequestDto
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.dtos.LoginResponseDto
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.services.LoginService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/login")
class LoginController(private val loginService: LoginService) {

    @PostMapping
    fun login(@RequestBody @Valid dto: LoginRequestDto): LoginResponseDto {
        return loginService.login(dto)
    }
}