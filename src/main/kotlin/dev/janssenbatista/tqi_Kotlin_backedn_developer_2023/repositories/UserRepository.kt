package dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.repositories

import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.models.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface UserRepository : JpaRepository<User, UUID> {
    fun findByEmail(email: String): Optional<User>
}