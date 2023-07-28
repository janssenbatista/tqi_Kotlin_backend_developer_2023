package dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.models

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.ZonedDateTime
import java.util.*

@Entity
@Table(name = "tb_users")
data class User(
    @Id
    val id: UUID? = UUID.randomUUID(),
    @Column(length = 50, nullable = false)
    var name: String,
    @Column(length = 100, nullable = false, unique = true)
    var email: String,
    @Column(nullable = false)
    var password: String,
    @Column(name = "role_id", nullable = false)
    val roleId: Int,
    @Column(name = "is_enabled")
    var isEnabled: Boolean = true,
    @JsonIgnore
    val createdAt: ZonedDateTime = ZonedDateTime.now(),
    @JsonIgnore
    var updatedAt: ZonedDateTime = ZonedDateTime.now()
)
