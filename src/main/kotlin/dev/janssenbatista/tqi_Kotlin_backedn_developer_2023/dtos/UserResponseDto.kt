package dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.dtos

import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.models.User
import java.time.ZonedDateTime
import java.util.UUID

data class UserResponseDto(
        val id: UUID,
        val name: String,
        val email: String,
        val createdAt: ZonedDateTime,
        val updatedAt: ZonedDateTime
) {
    companion object {
        fun fromEntity(user: User) =
                UserResponseDto(
                        id = user.id!!,
                        name = user.name,
                        email = user.email,
                        createdAt = user.createdAt,
                        updatedAt = user.updatedAt
                )
    }
}