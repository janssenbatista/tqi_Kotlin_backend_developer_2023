package dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.security

import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.enums.Role
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.models.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class UserDetailsImpl(private val user: User) : UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        val role = Role.values().find { it.value == user.roleId }
        return mutableListOf(SimpleGrantedAuthority("ROLE_${role!!.name}"))
    }

    override fun getPassword(): String =
            user.password

    override fun getUsername(): String =
            user.email

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = user.isEnabled
}