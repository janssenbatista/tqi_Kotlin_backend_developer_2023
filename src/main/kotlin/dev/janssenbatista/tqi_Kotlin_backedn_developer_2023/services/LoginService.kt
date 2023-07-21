package dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.services

import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.dtos.LoginRequestDto
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.dtos.LoginResponseDto
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.exceptions.InvalidEmailOrPasswordException
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.repositories.UserRepository
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.security.JwtService
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.security.UserDetailsImpl
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class LoginService(private val userRepository: UserRepository, private val jwtService: JwtService) {

    fun login(dto: LoginRequestDto): LoginResponseDto {
        val user = userRepository.findByEmail(dto.email).orElseThrow {
            InvalidEmailOrPasswordException("invalid email or password")
        }
        val validPassword = BCryptPasswordEncoder().matches(dto.password, user.password)
        if (!validPassword) {
            throw InvalidEmailOrPasswordException("invalid email or password")
        }
        val jwtToken = jwtService.generateToken(userDetails = UserDetailsImpl(user))
        return LoginResponseDto(token = jwtToken)
    }
}