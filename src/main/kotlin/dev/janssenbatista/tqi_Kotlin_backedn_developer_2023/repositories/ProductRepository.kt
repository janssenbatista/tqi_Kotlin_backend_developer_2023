package dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.repositories

import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.models.Product
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface ProductRepository : JpaRepository<Product, UUID> {
    fun findByName(name: String): Optional<Product>
}