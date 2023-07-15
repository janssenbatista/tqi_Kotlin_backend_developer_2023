package dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.security

import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.repositories.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserDetailsServiceImpl(private val userRepository: UserRepository) : UserDetailsService {
    override fun loadUserByUsername(username: String?): UserDetails {
        val user = userRepository.findByEmail(username!!).orElseThrow {
            throw UsernameNotFoundException("username not found")
        }
        return UserDetailsImpl(user)
    }
}